/*** Eclipse Class Decompiler plugin, copyright (c) 2016 Chen Chao (cnfree2000@hotmail.com) ***/
package uk.co.tfd.kindle.signalk;

import com.amazon.kindle.booklet.AbstractBooklet;
import com.amazon.kindle.booklet.BookletContext;
import com.amazon.kindle.booklet.ChromeHeaderRequest;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

public class SignalkBooklet extends AbstractBooklet implements ActionListener {

	private static final long serialVersionUID = 1L;
	// Handle the privilege hint prefix...
	private static String PRIVILEGE_HINT_PREFIX = "?";
    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        System.setProperty("org.slf4j.simpleLogger.logFile","/var/tmp/signalk.log");
        System.setProperty("org.slf4j.simpleLogger.showDateTime","true");
        System.setProperty("org.slf4j.simpleLogger.showShortLogName","true");
        log = LoggerFactory.getLogger(SignalkBooklet.class);
        Util.setKindle(true);
    }

    private static Logger log;



    private Container rootContainer = null;


	private Component status = null;
	private int depth = 0;
    private String configFile;

    public SignalkBooklet() {
        Util.setScreenResolution(Toolkit.getDefaultToolkit().getScreenResolution());

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
        log.info("create called with {} ", bookletContext);
        super.a(bookletContext);
    }

    // setChromeHeaderRequest( (ChromeHeaderRequest chromeHeaderReques );
    @Override
    public void a(ChromeHeaderRequest chromeHeaderRequest) {
        log.info("setChromeHeaderRequest called with {} ",chromeHeaderRequest);
        super.a(chromeHeaderRequest);
    }

    // 	public void start(URI contentURI)
    @Override
    public void b(URI uri) {
        log.info("start called with {} ", uri);
        configFile = uri.getPath();
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
	//				log.error("Failed to find getUIContainer method, abort!");
                    endBooklet();
					return null;
				}
				rootContainer = container;
				return container;
			} catch (Throwable t) {
				throw new RuntimeException(t.toString());
			}
		}
	}


	private void endBooklet() {
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
//            Runtime.getRuntime()
//                    .exec("lipc-set-prop com.lab126.appmgrd stop app://com.lab126.booklet.kindlet");
            log.info("Ending Booklet");
			Runtime.getRuntime().exec("lipc-set-prop com.lab126.appmgrd stop app://uk.co.tfd.kindle.signalk");
		} catch (IOException e) {
//			log.error("Failed when terminating ", e);
		}
	}



	private void longStart() {
		try {
			initializeUI(); // step 3
		} catch (Throwable t) {
            log.error(t.getMessage(), new RuntimeException(t));
			throw new RuntimeException(t);
		}
	}





	private void initializeUI() throws IOException, NoSuchMethodException, ParseException {


        //  log.debug("Screen size{} screen resolition {} ", Toolkit.getDefaultToolkit().getScreenSize(), Toolkit.getDefaultToolkit().getScreenResolution());
        //  Kindle PW4 sizejava.awt.Dimension[width=1072,height=1448] screen resolition 300
        log.debug("Starting Up");
		Container root = getUIContainer();


        log.debug("Starting Up1 {} ", root);
        log.debug("Component Count {} ", root.getComponentCount());
        log.debug("Components {} ", Arrays.toString(root.getComponents()));
        log.debug("Starting Up1 {} ", root);



        log.debug("Starting Up2");
        Font rootFont = new Font("Futura",Font.PLAIN, 10);
        Font titleFont = rootFont.deriveFont(Font.BOLD, (float)20);
        root.setFont(rootFont);

		MainScreen mainScreen = new MainScreen(root,
				configFile,
				new MainScreen.MainScreenExit() {

			@Override
			public void exit() {
				SignalkBooklet.this.endBooklet();
			}
		});
        listComponentTree(root,"->");
		mainScreen.start();
	}



    public void listComponentTree(Container root, String indent) {
        log.debug("{} ------------------------------", indent);
        for(Component c : root.getComponents()) {
            log.debug("{} {} ", indent, c);
            if ( c instanceof Container ) {
                listComponentTree((Container) c, indent+"->");
            }
        }
        log.debug("{} ------------------------------", indent);
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
        log.debug("Action Performed {} ", e);
    }

    /*
    @Override
    public JFrame ft(String s, String s1) {
        BookletContext var3 = this.aFn();

        log.debug("JFrame called with "+s+" "+s1);
        log.debug("Booklet Context is "+var3);

        try {
            if(var3 != null) {
                var3.bfB().postEvent(new LipcEvent(var3.bfB(), "testBookFrameCreated", (List)null));
            }
        } catch (LipcException var5) {
            var5.printStackTrace();
        }


        return var3 != null && var3.getName() != null? KindleFrameFactory.createKindleFrame(var3.getName(), s, s1):null;
    }

    @Override
    public void a(Runnable runnable, String s, boolean b) {

    }
    */


}
