/*** Eclipse Class Decompiler plugin, copyright (c) 2016 Chen Chao (cnfree2000@hotmail.com) ***/
package uk.co.tfd.kindle.signalk;

import com.amazon.kindle.booklet.AbstractBooklet;
import com.amazon.kindle.booklet.BookletContext;
import com.amazon.kindle.booklet.ChromeHeaderRequest;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;

public class SignalkBooklet extends AbstractBooklet implements ActionListener {

	private static final long serialVersionUID = 1L;
	// Handle the privilege hint prefix...
	private static String PRIVILEGE_HINT_PREFIX = "?";


	private Container rootContainer = null;


	private Component status = null;
	private int depth = 0;
	private static final Logger log = LoggerFactory.getLogger(SignalkBooklet.class);

	public SignalkBooklet() {
		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
		System.setProperty("org.slf4j.simpleLogger.logFile","/var/tmp/HelloWorld.log");

		new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    public void run() {
                        SignalkBooklet.this.longStart();
                    }
                },
                1000
        );

	}

    // Override obvuscated methods, this will change every time
    // create(BookletContext bookletContext)
    @Override
    public void a(BookletContext bookletContext) {
        super.a(bookletContext);
    }

    // setChromeHeaderRequest( (ChromeHeaderRequest chromeHeaderReques );
    @Override
    public void a(ChromeHeaderRequest chromeHeaderRequest) {
        super.a(chromeHeaderRequest);
    }

    // 	public void start(URI contentURI)
    @Override
    public void b(URI uri) {
        super.b(uri);
    }

    // Because this got obfuscated...
	private Container getUIContainer() {
		// Check our cached value, first
		if (rootContainer != null) {
			return rootContainer;
		} else {
			try {
				Container container = Util.getUIContainer(this);
				if (container == null) {
					log.error("Failed to find getUIContainer method, abort!");
					suicide(Util.obGetBookletContext(2, this));
					return null;
				}
				rootContainer = container;
				return container;
			} catch (Throwable t) {
				throw new RuntimeException(t.toString());
			}
		}
	}


	private void suicide(BookletContext context) {
		try {
			// Send a BACKWARD lipc event to background the app (-> stop())
			// NOTE: This has a few side-effects, since we effectively skip create & longStart
			//	 on subsequent start-ups, and we (mostly) never go to destroy().
			// NOTE: Incidentally, this is roughly what the [Home] button does on the Touch, so, for the same reason,
			//	 it's recommended not to tap Home on that device ;).
			// NOTE: Setting the unloadPolicy to unloadOnPause in the app's appreg properties takes care of that,
			//	 stop() then *always* leads to destroy() :).
			//Runtime.getRuntime().exec("lipc-set-prop com.lab126.appmgrd backward 0");
			// Send a STOP lipc event to exit the app (-> stop() -> destroy()). More closely mirrors the Kindlet lifecycle.
			Runtime.getRuntime().exec("lipc-set-prop com.lab126.appmgrd stop app://com.mobileread.ixtab.signalk");
		} catch (IOException e) {
			log.error("Failed when terminating ", e);
		}
	}



	private void longStart() {
		try {
			initializeUI(); // step 3
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}





	private void initializeUI() throws IOException, NoSuchMethodException, ParseException {
		Container root = getUIContainer();
		root.removeAll();
		MainScreen mainScreen = new MainScreen(root,
				"src/test/resources/config.yaml",
				new MainScreen.MainScreenExit() {

			@Override
			public void exit() {
				SignalkBooklet.this.suicide(Util.obGetBookletContext(2, SignalkBooklet.this));
			}
		});
		mainScreen.start();
	}








	private static int viewLevel = -1;
	private static int viewOffset = -1;


	public void destroy() {
		//new Logger().append("destroy()");
		// Try to cleanup behind us on exit...
		try {
			// NOTE: This can be a bit racey with stop(),
			//	 so sleep for a tiny bit so our commandToRunOnExit actually has a chance to run...
			Thread.sleep(175);
			Util.updateCCDB("HelloWorld", "/mnt/us/documents/HelloWorld.hello");
		} catch (Exception ignored) {
			// Avoid the framework shouting at us...
		}

		super.destroy();
	}



    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
