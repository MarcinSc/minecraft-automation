package com.gempukku.minecraft.automation.gui.computer.console;

import com.gempukku.minecraft.automation.lang.IllegalSyntaxException;
import com.gempukku.minecraft.automation.lang.parser.ScriptParser;

import java.io.IOException;
import java.io.StringReader;

public class CompileScriptOnTheFly {
	private volatile CompileStatus _compileStatus;
	private volatile String _scriptText;
	private volatile boolean _finishedEditing;

	private final Object _lockObject = new Object();

	public void startCompiler() {
		Thread thr = new Thread(
						new Runnable() {
							public void run() {
								keepCompilingUntilFinishedEditing();
							}
						});
		thr.start();
	}

	public void submitCompileRequest(String scriptText) {
		synchronized (_lockObject) {
			_scriptText = scriptText;
			_compileStatus = null;
			_lockObject.notifyAll();
		}
	}

	public CompileStatus getCompileStatus() {
		return _compileStatus;
	}

	public void finishedEditing() {
		_finishedEditing = true;
		synchronized (_lockObject) {
			_lockObject.notifyAll();
		}
	}

	private void keepCompilingUntilFinishedEditing() {
		while (!_finishedEditing) {
			String scriptText = null;
			synchronized (_lockObject) {
				scriptText = _scriptText;
				_scriptText = null;
			}

			CompileStatus newCompileStatus = null;
			if (scriptText != null) {
				ScriptParser parser = new ScriptParser();
				try {
					parser.parseScript(new StringReader(scriptText));
					newCompileStatus = new CompileStatus(true, null);
				} catch (IllegalSyntaxException exp) {
					newCompileStatus = new CompileStatus(false, exp);
				} catch (IOException exp) {
					// Can't really happen, as we use StringReader, but oh well
					newCompileStatus = new CompileStatus(false, null);
				} catch (RuntimeException exp) {
					newCompileStatus = new CompileStatus(false, null);
				}
			}

			synchronized (_lockObject) {
				// If new request was not created in the meantime, wait for notify
				if (_scriptText == null) {
					_compileStatus = newCompileStatus;
					try {
						_lockObject.wait();
					} catch (InterruptedException exp) {

					}
				}
			}
		}
	}

	public class CompileStatus {
		public final boolean success;
		public final IllegalSyntaxException error;

		public CompileStatus(boolean success, IllegalSyntaxException error) {
			this.error = error;
			this.success = success;
		}
	}
}
