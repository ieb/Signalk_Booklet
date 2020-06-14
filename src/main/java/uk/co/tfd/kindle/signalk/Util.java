package uk.co.tfd.kindle.signalk;

import com.amazon.kindle.booklet.AbstractBooklet;
import com.amazon.kindle.booklet.BookletContext;
import com.amazon.kindle.restricted.content.catalog.ContentCatalog;
import com.amazon.kindle.restricted.runtime.Framework;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


public class Util {


	public static BookletContext obGetBookletContext(int j, AbstractBooklet booklet){
		BookletContext bc = null;
		Method[] methods = AbstractBooklet.class.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getReturnType() == BookletContext.class) {
				// Double check that it takes no arguments, too...
				System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOO");
				Class[] params = methods[i].getParameterTypes();
				System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOO");
				if (params.length == 0) {
					try {
						System.out.println(i);
						System.out.println(methods[i]);
						System.out.println(methods[i].getReturnType().getName());
						System.out.println(methods[i].getName());
						System.out.println(methods[i].getParameterCount());
						System.out.println(booklet);
						System.out.println("---------------------------------------");
						bc = (BookletContext) methods[i].invoke(booklet, null);
						System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOO");
						System.out.println(bc);
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}
		}
		System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOO");
		return bc;
	}

	public static Container getUIContainer(AbstractBooklet booklet) throws InvocationTargetException, IllegalAccessException {

		Method getUIContainer = null;
		System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOO");
		// Should be the only method returning a Container in BookletContext...
		Method[] methods = BookletContext.class.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getReturnType() == Container.class) {
				// Double check that it takes no arguments, too...
				Class[] params = methods[i].getParameterTypes();
				if (params.length == 0) {
					getUIContainer = methods[i];
					System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOO");
					break;
				}
			}
		}


		if (getUIContainer != null) {

			System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOO");
			//new Logger().append("Found getUIContainer method as " + getUIContainer.toString());
			BookletContext bc = Util.obGetBookletContext(1, booklet);
			System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOO");
			Container rootContainer = (Container) getUIContainer.invoke(bc, null);
			System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOO");
			return rootContainer;
		}
		else {
			return null;
		}
	}

	// And this was always obfuscated...
	// NOTE: Pilfered from KPVBooklet (https://github.com/koreader/kpvbooklet/blob/master/src/com/github/chrox/kpvbooklet/ccadapter/CCAdapter.java)
	/**
	 * Perform CC request of type "query" and "change"
	 * @param req_type request type of "query" or "change"
	 * @param req_json request json string
	 * @return return json object
	 */
	private static JSONObject ccPerform(String req_type, String req_json) {
		ContentCatalog CC = (ContentCatalog) Framework.getService(ContentCatalog.class);
		try {
			Method perform = null;

			// Enumeration approach
			Class[] signature = {String.class, String.class, int.class, int.class};
			Method[] methods = ContentCatalog.class.getDeclaredMethods();
			for (int i = 0; i < methods.length; i++) {
				Class[] params = methods[i].getParameterTypes();
				if (params.length == signature.length) {
					int j;
					for (j = 0; j < signature.length && params[j].isAssignableFrom( signature[j] ); j++ ) {}
					if (j == signature.length) {
						perform = methods[i];
						break;
					}
				}
			}

			if (perform != null) {
				JSONObject json = (JSONObject) perform.invoke(CC, new Object[] { req_type, req_json, new Integer(200), new Integer(5) });
				return json;
			}
			else {
				new Logger().append("Failed to find perform method, last access time won't be set on exit!");
				return new JSONObject();
			}
		} catch (Throwable t) {
			throw new RuntimeException(t.toString());
		}
	}


	public static void updateCCDB(String tag, String path) {
		long lastAccess = new Date().getTime() / 1000L;
		path = JSONObject.escape(path);
		// NOTE: Hard-code the path, as no-one should be using a custom .kual trigger...
		String json_query = "{\"filter\":{\"Equals\":{\"value\":\"" + path + "\",\"path\":\"location\"}},\"type\":\"QueryRequest\",\"maxResults\":1,\"sortOrder\":[{\"order\":\"descending\",\"path\":\"lastAccess\"},{\"order\":\"ascending\",\"path\":\"titles[0].collation\"}],\"startIndex\":0,\"id\":1,\"resultType\":\"fast\"}";
		JSONObject json = Util.ccPerform("query", json_query);
		JSONArray values = (JSONArray) json.get("values");
		JSONObject value = (JSONObject) values.get(0);
		String uuid = (String) value.get("uuid");
		String json_change = "{\"commands\":[{\"update\":{\"uuid\":\"" + uuid + "\",\"lastAccess\":" + lastAccess + ",\"displayTags\":[\"" + tag + "\"]" + "}}],\"type\":\"ChangeRequest\",\"id\":1}";
		Util.ccPerform("change", json_change);
		//new Logger().append("Set KUAL's lastAccess ccdb entry to " + lastAccess);
	}

	public static <T> T option(Object v, T defaultValue) {
		if ( v == null ) {
			return defaultValue;
		}
		return (T) v;
	}

	public static <T> T option(Map<String, Object> map, String key, T defaultValue) {
		if ( map != null && map.containsKey(key)) {
			return (T) map.get(key);
		}
		return defaultValue;
	}
	public static <T> T required(Map<String, Object> map, String key) {
		if ( map != null && map.containsKey(key)) {
			return (T) map.get(key);
		}
		throw new IllegalArgumentException("Option "+key+" is required");
	}

    public static <T> T resolve(Map<String, Object> input, String path, T defaultValue) {
        String[] elements = path.split("\\.");
        Object o = input;
        for (int i = 0; i < elements.length-1; i++) {
            if ( o instanceof Map) {
                Map<Object, String> m = (Map<Object, String>) o;
                o = (T) m.get(elements[i]);
            } else {
                return defaultValue;
            }
        }
        if ( o instanceof Map) {
            Map<Object, String> m = (Map<Object, String>) o;
            return (T) m.get(elements[elements.length - 1]);
        } else {
            return defaultValue;
        }
    }

    /**
     * Created by ieb on 09/06/2020.
     */
    public enum HAlign {
        RIGHT, CENTER, LEFT
    }

    public enum VAlign {
        BOTTOM, TOP, BASELINE, CENTER;
    }


    public static void drawString(String s, int x, int y, Font font, HAlign halign, VAlign valign, Graphics2D g2) {
        if ( s != null) {
            g2.setFont(font);
            Rectangle2D r = font.getStringBounds(s, g2.getFontRenderContext());
            switch(halign) {
                case CENTER: x = x - (int)r.getCenterX(); break;
                case RIGHT: x = x - (int)r.getWidth(); break;
            }
            switch(valign) {
                case CENTER: y = y - (int)(r.getCenterY()); break;
                case TOP: y = y - (int)(r.getY()); break;
                case BOTTOM: y = y - (int)(r.getY() + r.getHeight()); break;
            }
            g2.drawString(s, x, y);
            //g2.drawRect(x,y+(int)r.getY(),(int)r.getWidth(),(int)r.getHeight());
        }

    }




}
