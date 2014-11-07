package home.learn.opc;


import javafish.clients.opc.JOpc;
import javafish.clients.opc.component.OpcGroup;
import javafish.clients.opc.component.OpcItem;
import javafish.clients.opc.exception.CoInitializeException;
import javafish.clients.opc.exception.CoUninitializeException;
import javafish.clients.opc.exception.ComponentNotFoundException;
import javafish.clients.opc.exception.ConnectivityException;
import javafish.clients.opc.exception.SynchReadException;
import javafish.clients.opc.exception.SynchWriteException;
import javafish.clients.opc.exception.UnableAddGroupException;
import javafish.clients.opc.exception.UnableAddItemException;
import javafish.clients.opc.exception.UnableRemoveGroupException;
import javafish.clients.opc.exception.UnableRemoveItemException;
import javafish.clients.opc.variant.Variant;
import junit.framework.TestCase;

/**
 * OPC Test class.
 *
 */
public class OpcTest extends TestCase {

	/** The opc. */
	private JOpc opc;
	
	/** The group. */
	private OpcGroup group;
	
	/** The item. */
	private OpcItem item;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() {

		// Initialize OPC. 
		opc = new JOpc("localhost", "Kepware.KEPServerEX.V5", "JOPC1");

		// Initialize name item.
		item = new OpcItem("Channel1.Device1.name", true, "");

		// Initialize excel group. 
		group = new OpcGroup("excel", true, 100, 0.0f);

		// Add item to group.
		group.addItem(item);

		try {
			JOpc.coInitialize();
		} catch (CoInitializeException e) {
			fail(e.getMessage());
		}

		try {
			// Connect to OPC Server
			opc.connect();
		} catch (ConnectivityException e) {
			fail("Can't connect to the OPC Server");
			fail(e.getMessage());
		}

		// Add group to opc.
		opc.addGroup(group);

		try {
			// Register the group to opc. 
			opc.registerGroup(group);
		} catch (ComponentNotFoundException e) {
			fail("Can't register group");
			fail(e.getMessage());
		} catch (UnableAddGroupException e) {
			fail("Can't register group");
			fail(e.getMessage());
		}

		try {
			// Register item to the opc. 
			opc.registerItem(group, item);
		} catch (ComponentNotFoundException e) {
			fail("Can't register item");
			fail(e.getMessage());
		} catch (UnableAddItemException e) {
			fail("Can't register item");
			fail(e.getMessage());
		}

	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() {
		try {
			opc.unregisterItem(group, item);
			opc.unregisterGroup(group);
			JOpc.coUninitialize();
		} catch (CoUninitializeException e) {
			fail(e.getMessage());
		} catch (ComponentNotFoundException e) {
			fail(e.getMessage());
		} catch (UnableRemoveItemException e) {
			fail(e.getMessage());
		} catch (UnableRemoveGroupException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test write read.
	 */
	public void testWriteRead() {

		// Set item value. 
		item.setValue(new Variant("HPES"));

		// Write to opc-server
		try {
			opc.synchWriteItem(group, item);
			assertTrue(true);
		} catch (ComponentNotFoundException e) {
			assertTrue(false);
			fail(e.getMessage());
		} catch (SynchWriteException e) {
			assertTrue(false);
			fail(e.getMessage());
		}

		OpcItem itemRead = null;
		for (int i = 0; i < 2; i++) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}

			// Read from opc-server
			try {
				itemRead = opc.synchReadItem(group, item);
			} catch (ComponentNotFoundException e) {
				fail(e.getMessage());
			} catch (SynchReadException e) {
				fail(e.getMessage());
			}
			assertEquals(itemRead.getValue().getString(), item.getValue()
					.getString());
		}

	}

}
