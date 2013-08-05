/******************************************************************************************
 * COPYRIGHT:                                                                             *
 * Universitat Politecnica de Valencia 2013                                               *
 * Camino de Vera, s/n                                                                    *
 * 46022 Valencia, Spain                                                                  *
 * www.upv.es                                                                             *
 *                                                                                        * 
 * D I S C L A I M E R:                                                                   *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)      *
 * in the context of the european funded FITTEST project (contract number ICT257574)      *
 * of which the UPV is the coordinator. As the sole developer of this source code,        *
 * following the signed FITTEST Consortium Agreement, the UPV should decide upon an       *
 * appropriate license under which the source code will be distributed after termination  *
 * of the project. Until this time, this code can be used by the partners of the          *
 * FITTEST project for executing the tasks that are outlined in the Description of Work   *
 * (DoW) that is annexed to the contract with the EU.                                     *
 *                                                                                        * 
 * Although it has already been decided that this code will be distributed under an open  *
 * source license, the exact license has not been decided upon and will be announced      *
 * before the end of the project. Beware of any restrictions regarding the use of this    *
 * work that might arise from the open source license it might fall under! It is the      *
 * UPV's intention to make this work accessible, free of any charge.                      *
 *****************************************************************************************/

/**
 *  @author Sebastian Bauersfeld
 */
package org.fruit.alayer.macosx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public final class AX {

	public static interface IAXObserverCB{
		public void callback(long axElement, String notification, Object data);
	}
	
    static{ loadLib("libax.jnilib"); }

    private static void loadLib(String name) {
        try {
			String unique = "blajsdlfjalsf";
			InputStream in = AX.class.getResourceAsStream(name);
			File fileOut = new File(System.getProperty("java.io.tmpdir") + "/" + name + unique);
			//System.out.println("Writing dll to: " + fileOut.getAbsolutePath());
			OutputStream out = new FileOutputStream(fileOut);
			copy(in, out);
			in.close();
			out.close();
			System.load(fileOut.toString());
			fileOut.deleteOnExit();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    private static void copy(InputStream in, OutputStream out){
        try{
            while(true){
                int data = in.read();
                if(data == -1)
                    break;
                out.write(data);
            }
        }catch(Exception exco){
            System.out.println(exco);
        }
    }

    // AXValueType Constants
    public static final long kAXValueCGPointType = 1;
    public static final long kAXValueCGSizeType = 2;
    public static final long kAXValueCGRectType = 3;
    public static final long kAXValueCFRangeType = 4;
    public static final long kAXValueAXErrorType = 5;
    public static final long kAXValueIllegalType = 0;
    
    // Attribute Constants
    public static final String kAXIdentifierAttribute				 = "AXIdentifier";
    public static final String kAXRoleAttribute				 = "AXRole";
    public static final String kAXSubroleAttribute				 = "AXSubrole";
    public static final String kAXRoleDescriptionAttribute			 = "AXRoleDescription";
    public static final String kAXHelpAttribute				 = "AXHelp";
    public static final String kAXTitleAttribute				 = "AXTitle";
    public static final String kAXValueAttribute				 = "AXValue";
    public static final String kAXValueDescriptionAttribute     = "AXValueDescription";
    public static final String kAXMinValueAttribute				 = "AXMinValue";
    public static final String kAXMaxValueAttribute				 = "AXMaxValue";
    public static final String kAXValueIncrementAttribute			 = "AXValueIncrement";
    public static final String kAXAllowedValuesAttribute			 = "AXAllowedValues";
    public static final String kAXPlaceholderValueAttribute			 = "AXPlaceholderValue";
    public static final String kAXEnabledAttribute				 = "AXEnabled";
    public static final String kAXFocusedAttribute				 = "AXFocused";
    public static final String kAXParentAttribute				 = "AXParent";
    public static final String kAXChildrenAttribute				 = "AXChildren";
    public static final String kAXSelectedChildrenAttribute			 = "AXSelectedChildren";
    public static final String kAXVisibleChildrenAttribute			 = "AXVisibleChildren";
    public static final String kAXWindowAttribute				 = "AXWindow";
    public static final String kAXTopLevelUIElementAttribute			 = "AXTopLevelUIElement";
    public static final String kAXPositionAttribute				 = "AXPosition";
    public static final String kAXSizeAttribute				 = "AXSize";
    public static final String kAXOrientationAttribute				 = "AXOrientation";
    public static final String kAXDescriptionAttribute			 = "AXDescription";
    public static final String kAXDescription					 = "AXDescription"; // old name
    public static final String kAXSelectedTextAttribute			 = "AXSelectedText";
    public static final String kAXSelectedTextRangeAttribute			 = "AXSelectedTextRange";
    public static final String kAXSelectedTextRangesAttribute			 = "AXSelectedTextRanges";
    public static final String kAXVisibleCharacterRangeAttribute		 = "AXVisibleCharacterRange";
    public static final String kAXNumberOfCharactersAttribute			 = "AXNumberOfCharacters";
    public static final String kAXSharedTextUIElementsAttribute		 = "AXSharedTextUIElements";
    public static final String kAXSharedCharacterRangeAttribute		 = "AXSharedCharacterRange";
    public static final String kAXInsertionPointLineNumberAttribute		 = "AXInsertionPointLineNumber";
    public static final String kAXMainAttribute				 = "AXMain";
    public static final String kAXMinimizedAttribute				 = "AXMinimized";
    public static final String kAXCloseButtonAttribute				 = "AXCloseButton";
    public static final String kAXZoomButtonAttribute				 = "AXZoomButton";
    public static final String kAXMinimizeButtonAttribute			 = "AXMinimizeButton";
    public static final String kAXToolbarButtonAttribute			 = "AXToolbarButton";
    public static final String kAXProxyAttribute				 = "AXProxy";
    public static final String kAXGrowAreaAttribute				 = "AXGrowArea";
    public static final String kAXModalAttribute				 = "AXModal";
    public static final String kAXDefaultButtonAttribute			 = "AXDefaultButton";
    public static final String kAXCancelButtonAttribute			 = "AXCancelButton";
    public static final String kAXMenuItemCmdCharAttribute			 = "AXMenuItemCmdChar";
    public static final String kAXMenuItemCmdVirtualKeyAttribute		 = "AXMenuItemCmdVirtualKey";
    public static final String kAXMenuItemCmdGlyphAttribute			 = "AXMenuItemCmdGlyph";
    public static final String kAXMenuItemCmdModifiersAttribute		 = "AXMenuItemCmdModifiers";
    public static final String kAXMenuItemMarkCharAttribute			 = "AXMenuItemMarkChar";
    public static final String kAXMenuItemPrimaryUIElementAttribute		 = "AXMenuItemPrimaryUIElement";
    public static final String kAXMenuBarAttribute				 = "AXMenuBar";
    public static final String kAXWindowsAttribute				 = "AXWindows";
    public static final String kAXFrontmostAttribute				 = "AXFrontmost";
    public static final String kAXHiddenAttribute				 = "AXHidden";
    public static final String kAXMainWindowAttribute				 = "AXMainWindow";
    public static final String kAXFocusedWindowAttribute			 = "AXFocusedWindow";
    public static final String kAXFocusedUIElementAttribute			 = "AXFocusedUIElement"; 
    public static final String kAXHeaderAttribute				 = "AXHeader";
    public static final String kAXEditedAttribute				 = "AXEdited";
    public static final String kAXValueWrapsAttribute				 = "AXValueWraps";
    public static final String kAXTabsAttribute				 = "AXTabs";
    public static final String kAXTitleUIElementAttribute			 = "AXTitleUIElement";
    public static final String kAXHorizontalScrollBarAttribute			 = "AXHorizontalScrollBar";
    public static final String kAXVerticalScrollBarAttribute			 = "AXVerticalScrollBar";
    public static final String kAXOverflowButtonAttribute			 = "AXOverflowButton";
    public static final String kAXFilenameAttribute				 = "AXFilename";
    public static final String kAXExpandedAttribute				 = "AXExpanded";
    public static final String kAXSelectedAttribute				 = "AXSelected";
    public static final String kAXSplittersAttribute				 = "AXSplitters";
    public static final String kAXNextContentsAttribute			 = "AXNextContents";
    public static final String kAXDocumentAttribute				 = "AXDocument";
    public static final String kAXDecrementButtonAttribute			 = "AXDecrementButton";
    public static final String kAXIncrementButtonAttribute			 = "AXIncrementButton";
    public static final String kAXPreviousContentsAttribute			 = "AXPreviousContents";
    public static final String kAXContentsAttribute				 = "AXContents";
    public static final String kAXIncrementorAttribute				 = "AXIncrementor";
    public static final String kAXHourFieldAttribute				 = "AXHourField";
    public static final String kAXMinuteFieldAttribute				 = "AXMinuteField";
    public static final String kAXSecondFieldAttribute				 = "AXSecondField";
    public static final String kAXAMPMFieldAttribute				 = "AXAMPMField";
    public static final String kAXDayFieldAttribute				 = "AXDayField";
    public static final String kAXMonthFieldAttribute				 = "AXMonthField";
    public static final String kAXYearFieldAttribute				 = "AXYearField";
    public static final String kAXColumnTitleAttribute				 = "AXColumnTitles";
    public static final String kAXURLAttribute					 = "AXURL";
    public static final String kAXLabelUIElementsAttribute			 = "AXLabelUIElements";
    public static final String kAXLabelValueAttribute				 = "AXLabelValue";
    public static final String kAXShownMenuUIElementAttribute                   = "AXShownMenuUIElement";
    public static final String kAXServesAsTitleForUIElementsAttribute           = "AXServesAsTitleForUIElements";
    public static final String kAXLinkedUIElementsAttribute 			 = "AXLinkedUIElements";
    public static final String kAXRowsAttribute				 = "AXRows";
    public static final String kAXVisibleRowsAttribute				 = "AXVisibleRows";
    public static final String kAXSelectedRowsAttribute			 = "AXSelectedRows";
    public static final String kAXColumnsAttribute				 = "AXColumns";
    public static final String kAXVisibleColumnsAttribute			 = "AXVisibleColumns";
    public static final String kAXSelectedColumnsAttribute			 = "AXSelectedColumns";
    public static final String kAXSortDirectionAttribute			 = "AXSortDirection";
    public static final String kAXIndexAttribute				 = "AXIndex";
    public static final String kAXDisclosingAttribute				 = "AXDisclosing";
    public static final String kAXDisclosedRowsAttribute			 = "AXDisclosedRows";
    public static final String kAXDisclosedByRowAttribute			 = "AXDisclosedByRow";
    public static final String kAXDisclosureLevelAttribute			 = "AXDisclosureLevel";
    public static final String kAXMatteHoleAttribute				 = "AXMatteHole";
    public static final String kAXMatteContentUIElementAttribute		 = "AXMatteContentUIElement";
    public static final String kAXMarkerUIElementsAttribute			 = "AXMarkerUIElements";
    public static final String kAXUnitsAttribute				 = "AXUnits";
    public static final String kAXUnitDescriptionAttribute			 = "AXUnitDescription";
    public static final String kAXMarkerTypeAttribute				 = "AXMarkerType";
    public static final String kAXMarkerTypeDescriptionAttribute		 = "AXMarkerTypeDescription";
    public static final String kAXIsApplicationRunningAttribute		 = "AXIsApplicationRunning";
    public static final String kAXSearchButtonAttribute			 = "AXSearchButton";
    public static final String kAXClearButtonAttribute				 = "AXClearButton";
    public static final String kAXFocusedApplicationAttribute			 = "AXFocusedApplication";
    public static final String kAXRowCountAttribute				 = "AXRowCount";
    public static final String kAXColumnCountAttribute				 = "AXColumnCount";
    public static final String kAXOrderedByRowAttribute			 = "AXOrderedByRow";
    public static final String kAXWarningValueAttribute			 = "AXWarningValue";
    public static final String kAXCriticalValueAttribute			 = "AXCriticalValue";
    public static final String kAXSelectedCellsAttribute			 = "AXSelectedCells";
    public static final String kAXVisibleCellsAttribute			 = "AXVisibleCells";
    public static final String kAXRowHeaderUIElementsAttribute			 = "AXRowHeaderUIElements";
    public static final String kAXColumnHeaderUIElementsAttribute		 = "AXColumnHeaderUIElements";
    public static final String kAXRowIndexRangeAttribute			 = "AXRowIndexRange";
    public static final String kAXColumnIndexRangeAttribute			 = "AXColumnIndexRange";
    public static final String kAXHorizontalUnitsAttribute			 = "AXHorizontalUnits";
    public static final String kAXVerticalUnitsAttribute			 = "AXVerticalUnits";
    public static final String kAXHorizontalUnitDescriptionAttribute		 = "AXHorizontalUnitDescription";
    public static final String kAXVerticalUnitDescriptionAttribute		 = "AXVerticalUnitDescription";
    public static final String kAXHandlesAttribute				 = "AXHandles";
    public static final String kAXTextAttribute				 = "AXText";
    public static final String kAXVisibleTextAttribute				 = "AXVisibleText";
    public static final String kAXIsEditableAttribute				 = "AXIsEditable";
    public static final String kAXColumnTitlesAttribute			 = "AXColumnTitles";
    public static final String kAXLineForIndexParameterizedAttribute		 = "AXLineForIndex";
    public static final String kAXRangeForLineParameterizedAttribute          	 = "AXRangeForLine";
    public static final String kAXStringForRangeParameterizedAttribute        	 = "AXStringForRange";
    public static final String kAXRangeForPositionParameterizedAttribute	 = "AXRangeForPosition";
    public static final String kAXRangeForIndexParameterizedAttribute 		 = "AXRangeForIndex";
    public static final String kAXBoundsForRangeParameterizedAttribute		 = "AXBoundsForRange";
    public static final String kAXRTFForRangeParameterizedAttribute		 = "AXRTFForRange";
    public static final String kAXAttributedStringForRangeParameterizedAttribute  = "AXAttributedStringForRange";
    public static final String kAXStyleRangeForIndexParameterizedAttribute	 = "AXStyleRangeForIndex";
    public static final String kAXCellForColumnAndRowParameterizedAttribute		 = "AXCellForColumnAndRow";
    public static final String kAXLayoutPointForScreenPointParameterizedAttribute	 = "AXLayoutPointForScreenPoint";
    public static final String kAXLayoutSizeForScreenSizeParameterizedAttribute	 = "AXLayoutSizeForScreenSize";
    public static final String kAXScreenPointForLayoutPointParameterizedAttribute	 = "AXScreenPointForLayoutPoint";
    public static final String kAXScreenSizeForLayoutSizeParameterizedAttribute	 = "AXScreenSizeForLayoutSize";
    
    
    // Error Constants    
    public static final int kAXErrorSuccess                 = 0;
    public static final int kAXErrorFailure             = -25200;
    public static final int kAXErrorIllegalArgument         = -25201;
    public static final int kAXErrorInvalidUIElement            = -25202;
    public static final int kAXErrorInvalidUIElementObserver        = -25203;
    public static final int kAXErrorCannotComplete          = -25204;
    public static final int kAXErrorAttributeUnsupported        = -25205;
    public static final int kAXErrorActionUnsupported           = -25206;
    public static final int kAXErrorNotificationUnsupported     = -25207;
    public static final int kAXErrorNotImplemented          = -25208;
    public static final int kAXErrorNotificationAlreadyRegistered   = -25209;
    public static final int kAXErrorNotificationNotRegistered       = -25210;
    public static final int kAXErrorAPIDisabled             = -25211;
    public static final int kAXErrorNoValue             = -25212;
    public static final int kAXErrorParameterizedAttributeUnsupported   = -25213;
    public static final int kAXErrorNotEnoughPrecision  = -25214;

    
    // focus notifications
    public static final String kAXMainWindowChangedNotification		= "AXMainWindowChanged";
    public static final String kAXFocusedWindowChangedNotification		= "AXFocusedWindowChanged";
    public static final String kAXFocusedUIElementChangedNotification		= "AXFocusedUIElementChanged";

    // application notifications
    public static final String kAXApplicationActivatedNotification		= "AXApplicationActivated";
    public static final String kAXApplicationDeactivatedNotification		= "AXApplicationDeactivated";
    public static final String kAXApplicationHiddenNotification		= "AXApplicationHidden";
    public static final String kAXApplicationShownNotification			= "AXApplicationShown";

    // window notifications
    public static final String kAXWindowCreatedNotification			= "AXWindowCreated";
    public static final String kAXWindowMovedNotification			= "AXWindowMoved";
    public static final String kAXWindowResizedNotification			= "AXWindowResized";
    public static final String kAXWindowMiniaturizedNotification		= "AXWindowMiniaturized";
    public static final String kAXWindowDeminiaturizedNotification		= "AXWindowDeminiaturized";

    // new drawer, sheet, and help tag notifications
    public static final String kAXDrawerCreatedNotification			= "AXDrawerCreated";
    public static final String kAXSheetCreatedNotification			= "AXSheetCreated";
    public static final String kAXHelpTagCreatedNotification			= "AXHelpTagCreated";

    // element notifications
    public static final String kAXValueChangedNotification			= "AXValueChanged";
    public static final String kAXUIElementDestroyedNotification		= "AXUIElementDestroyed";

    // menu notifications
    public static final String kAXMenuOpenedNotification			= "AXMenuOpened";
    public static final String kAXMenuClosedNotification			= "AXMenuClosed";
    public static final String kAXMenuItemSelectedNotification			= "AXMenuItemSelected";

    // table/outline notifications
    public static final String kAXRowCountChangedNotification			= "AXRowCountChanged";

    // outline notifications
    public static final String kAXRowExpandedNotification			= "AXRowExpanded";
    public static final String kAXRowCollapsedNotification			= "AXRowCollapsed";

    // cell-based table notifications
    public static final String kAXSelectedCellsChangedNotification		= "AXSelectedCellsChanged";

    // layout area notifications
    public static final String kAXUnitsChangedNotification			= "AXUnitsChanged";  
    public static final String kAXSelectedChildrenMovedNotification		= "AXSelectedChildrenMoved";

    // other notifications
    public static final String kAXSelectedChildrenChangedNotification		= "AXSelectedChildrenChanged";
    public static final String kAXResizedNotification				= "AXResized";
    public static final String kAXMovedNotification				= "AXMoved";
    public static final String kAXCreatedNotification				= "AXCreated";
    public static final String kAXSelectedRowsChangedNotification	= "AXSelectedRowsChanged";
    public static final String kAXSelectedColumnsChangedNotification	= "AXSelectedColumnsChanged";
    public static final String kAXSelectedTextChangedNotification	= "AXSelectedTextChanged";
    public static final String kAXTitleChangedNotification             = "AXTitleChangedNotification";

    public static native boolean AXAPIEnabled();
    public static native int AXUIElementCopyBooleanAttributeValue(long axElement, String attr);
    public static native long AXUIElementCopyReferenceAttributeValue(long axElement, String attr);
    public static native String AXUIElementCopyStringAttributeValue(long axElement, String attr);
    public static native long[] AXUIElementCopyReferenceArrayAttributeValue(long axElement, String attr);
    public static native long AXUIElementCreateSystemWide();
    public static native long AXUIElementGetTypeID();
    public static native long AXValueGetTypeID();
    public static native long CFBooleanGetTypeID();
    public static native long CFArrayGetTypeID();
    public static native long CFStringGetTypeID();
    public static native boolean CFBooleanGetValue(long cfTypeRef);
    public static native String CFStringGetCString(long cfStringRef);
    public static native void CFRelease(long object);
    public static native boolean CFEqual(long object1, long object2);
    public static native long CFHash(long object);
    public static native long CFRetain(long object);
    public static native long CFGetRetainCount(long cfTypeRef);
    public static native long CFGetTypeID(long cfTypeRef);
    public static native long AXUIElementCreateApplication(long pid);
    public static native long AXUIElementCopyElementAtPosition(long axParent, float x, float y);
    public static native long[] runningApplications();
    public static native String getAppName(long pid);
    public static native String[] AXUIElementCopyAttributeNames(long axElement);
    public static native double[] getRect(long axElement);
    public static native double[] getPos(long axElement);
    public static native double[] getSize(long axElement);
    public static native int AXUIElementIsAttributeSettable(long axElement, String attribute);
    public static native long AXUIElementGetPid(long axElement);
    

    public static int canObtainFocus(long axElement){
    	return AXUIElementIsAttributeSettable(axElement, kAXFocusedAttribute);
    }
    
    public static long getPID(String appName){
    	if(appName == null)
    		throw new IllegalArgumentException();
    	
    	for(long pid : runningApplications()){
    		if(appName.equals(getAppName(pid)))
    			return pid;
    	}
    	return 0;
    }
        
    public static final long[] EmptyRefArray = {};

    public static boolean dbool(int value){ return value > 0; }
    public static String dstring(String s){ return s == null ? "" : s; }
    public static long[] dlongarray(long[] array){ return array == null ? EmptyRefArray : array; }
    
    
    public static native long[] AXUIElementCopyMultipleAttributeValues(long axElement, String[] attributes);
    public static native long AXValueGetType(long axValueRef);

    //public static native long AXValueGetValue(AXValueRef value, AXValueType theType, void *valuePtr);

    
    
    // =================== CFRunLoop and AX Events ======================================
    public static native long AXObserverCreate(long pid);
    public static native long AXObserverGetRunLoopSource(long observer);
    public static native long CFRunLoopGetCurrent();
    public static native long CFRunLoopGetMain();
    public static native void CFRunLoopAddSource(long runLoop, long runLoopSource, String mode);
    public static native long AXObserverAddNotification(long observer, long axElement, String notification, IAXObserverCB cb);
    public static native void CFRunLoopRun();
    

    // ================== Quartz drawing operations =======================
    public static native void runApplication();
    public static native long createOverlayWindow();
    public static native void closeWindow(long nsWindow);
    public static native void setWindowFrame(long nsWindow, float x, float y, float width, float height);
    public static native long createCGContext(long nsWindow);
    public static native void CGContextFlush(long context);
    public static native void CGContextSetRGBStrokeColor(long context, float red, float green, float blue, float alpha);
    public static native void CGContextSetRGBFillColor(long context, float red, float green, float blue, float alpha);
    public static native void CGContextSetLineWidth(long context, float width);
    public static native void CGContextStrokeRect(long context, float x, float y, float width, float height);
    public static native void CGContextStrokeLine(long context, float x1, float y1, float x2, float y2);
    public static native void CGContextFillRect(long context, float x, float y, float width, float height);
    public static native void CGContextClearRect(long context, float x, float y, float width, float height);
    public static native void CGContextFillEllipseInRect(long context, float x, float y, float width, float height);
    public static native void CGContextStrokeEllipseInRect(long context, float x, float y, float width, float height);
    public static native void CGContextSelectFont(long context, String fontName, float size, int encoding);
    public static native void CGContextShowTextAtPoint(long context, float x, float y, String text);
    public static native void CGContextSetLineCap(long context, long style);
    
    // screens
    public static native long[] createConnectedScreens();
    public static native long createMainScreen();   // this is the screen with (0, 0) coordinates    
    public static native float[] getScreenFrame(long screen);
    

    // launch another process (first argument does NOT need to be the filename of the executable, 
    // because Apple's NSTask is used for implementation)
    public static native long launchTask(String cmd, String[] args);


    // determines whether a user app is still running
    public static native int terminated(long pid);

    // try to hide an application
    public static native int hide(long pid);

    // determine wheather an app is hidden
    public static native int hidden(long pid);

    // try to unhide an application
    public static native int unhide(long pid);

    // try to activate an application (make it frontmost)
    public static native int activate(long pid);

    // determine whether an app is frontmost
    public static native int active(long pid);

    // tries to terminate an application
    public static native int terminate(long pid);

    // tries to terminate an application (but harder ;-) )
    public static native int forceTerminate(long pid);

    // determines whether an app finished the launching process
    public static native int finishedLaunching(long pid);
    
    
    
	public static native long errno();
	public static native String strerror(long err);
	public static native long execve(String command, String[] argvc, String[] env, long[] fds); 
	public static native long[] pipe();
	public static native long close(long fd);
	public static native byte[] read(long fd, int numbytes);
	public static native long write(long fd, byte[] data);
	public static native long open(String path, long flags, long mode);    
    public static native long[] waitpid(long pid, long options);
	public static native boolean WIFEXITED(long status);
	public static native long WEXITSTATUS(long status);
	public static native boolean WIFSIGNALED(long status);
	public static native long WTERMSIG(long status);
	public static native boolean WIFCORED(long status);
	public static native boolean WCOREDUMP(long status);
	public static native boolean WCORESIG(long status);
	public static native boolean WIFSTOPPED(long status);
	public static native long WSTOPSIG(long status);
	public static native boolean WIFCONTINUED(long status);
	public static native long fcntl(long fd, long cmd, long arg);
	
	
	public static final long F_DUPFD = 0;
	public static final long F_GETFD = 1;
	public static final long F_SETFD = 2;
	public static final long F_GETFL = 3;
	public static final long F_SETFL = 4;
	public static final long F_GETLK = 5;
	public static final long F_SETLK = 6;
	public static final long F_SETLKW = 7;
	public static final long F_FREESP = 8;
	public static final long FD_CLOEXEC = 1;
	public static final long F_RDLCK = 1;
	public static final long F_WRLCK = 2;
	public static final long F_UNLCK = 3;
	public static final long O_CREAT = 00100;
	public static final long O_EXCL = 00200;
	public static final long O_NOCTTY = 00400;
	public static final long O_TRUNC = 01000;
	public static final long O_APPEND = 02000;
	public static final long O_NONBLOCK = 4;
	public static final long O_RDONLY = 0;
	public static final long O_WRONLY = 1;
	public static final long O_RDWR = 2;
	public static final long O_ACCMODE = 03;
	public static final long LOCK_SH = F_RDLCK;
	public static final long LOCK_EX = F_WRLCK;
	public static final long LOCK_NB = 0x0080;
	public static final long LOCK_UN = F_UNLCK;


	public static final long WNOHANG = 1;
	public static final long WUNTRACED = 2;

	
	// kill constants
    public static final long SIGHUP = 1 ; /* hangup */
    public static final long SIGINT = 2 ; /* interrupt */
    public static final long SIGQUIT= 3 ; /* quit */
    public static final long SIGILL = 4 ; /* illegal instruction (not reset when caught) */
    public static final long SIGTRAP= 5 ; /* trace trap (not reset when caught) */
    public static final long SIGABRT= 6 ; /* abort() */
    public static final long SIGPOLL= 7 ; /* pollable event ([XSR] generated, not supported) */
    public static final long SIGIOT = SIGABRT; /* compatibility */
    public static final long SIGEMT = 7 ; /* EMT instruction */
    public static final long SIGFPE = 8 ; /* floating polong exception */
    public static final long SIGKILL= 9 ; /* kill (cannot be caught or ignored) */
    public static final long SIGBUS = 10; /* bus error */
    public static final long SIGSEGV= 11; /* segmentation violation */
    public static final long SIGSYS = 12; /* bad argument to system call */
    public static final long SIGPIPE= 13; /* write on a pipe with no one to read it */
    public static final long SIGALRM= 14; /* alarm clock */
    public static final long SIGTERM= 15; /* software termination signal from kill */
    public static final long SIGURG = 16; /* urgent condition on IO channel */
    public static final long SIGSTOP= 17; /* sendable stop signal not from tty */
    public static final long SIGTSTP= 18; /* stop signal from tty */
    public static final long SIGCONT= 19; /* continue a stopped process */
    public static final long SIGCHLD= 20; /* to parent on child stop or exit */
    public static final long SIGTTIN= 21; /* to readers pgrp upon background tty read */
    public static final long SIGTTOU= 22; /* like TTIN for output if (tp->t_local&LTOSTOP) */
    public static native long kill(long pid, long signal);

    
    // error constants
    public static final long EPERM = 1; /* Operation not permitted */
    public static final long ENOENT = 2; /* No such file or directory */
    public static final long ESRCH = 3; /* No such process */
    public static final long EINTR = 4; /* Interrupted system call */
    public static final long EIO = 5; /* I/O error */
    public static final long ENXIO = 6; /* No such device or address */
    public static final long E2BIG = 7; /* Arg list too long */
    public static final long ENOEXEC = 8; /* Exec format error */
    public static final long EBADF = 9; /* Bad file number */
    public static final long ECHILD = 10; /* No child processes */
    public static final long EDEADLK = 11; /* Resource deadlock would occur */
    public static final long ENOMEM = 12; /* Out of memory */
    public static final long EACCES = 13; /* Permission denied */
    public static final long EFAULT = 14; /* Bad address */
    public static final long ENOTBLK = 15; /* Block device required */
    public static final long EBUSY = 16; /* Device or resource busy */
    public static final long EEXIST = 17; /* File exists */
    public static final long EXDEV = 18; /* Cross-device link */
    public static final long ENODEV = 19; /* No such device */
    public static final long ENOTDIR = 20; /* Not a directory */
    public static final long EISDIR = 21; /* Is a directory */
    public static final long EINVAL = 22; /* Invalid argument */
    public static final long ENFILE = 23; /* File table overflow */
    public static final long EMFILE = 24; /* Too many open files */
    public static final long ENOTTY = 25; /* Not a typewriter */
    public static final long ETXTBSY = 26; /* Text file busy */
    public static final long EFBIG = 27; /* File too large */
    public static final long ENOSPC = 28; /* No space left on device */
    public static final long ESPIPE = 29; /* Illegal seek */
    public static final long EROFS = 30; /* Read-only file system */
    public static final long EMLINK = 31; /* Too many links */
    public static final long EPIPE = 32; /* Broken pipe */
    public static final long EDOM = 33; /* Math argument out of domain of func */
    public static final long ERANGE = 34; /* Math result not representable */
    public static final long EAGAIN = 35; /* Try again */
    public static final long EWOULDBLOCK = EAGAIN; /* Operation would block */
    public static final long EINPROGRESS = 36; /* Operation now in progress */
    public static final long EALREADY = 37; /* Operation already in progress */
    public static final long ENOTSOCK = 38; /* Socket operation on non-socket */
    public static final long EDESTADDRREQ = 39; /* Destination address required */
    public static final long EMSGSIZE = 40; /* Message too long */
    public static final long EPROTOTYPE = 41; /* Protocol wrong type for socket */
    public static final long ENOPROTOOPT = 42; /* Protocol not available */
    public static final long EPROTONOSUPPORT = 43; /* Protocol not supported */
    public static final long ESOCKTNOSUPPORT = 44; /* Socket type not supported */
    public static final long EOPNOTSUPP = 45; /* Operation not supported on transport endpoint */
    public static final long ENOTSUP = EOPNOTSUPP; /* Operation not supported on transport endpoint */
    public static final long EPFNOSUPPORT = 46; /* Protocol family not supported */
    public static final long EAFNOSUPPORT = 47; /* Address family not supported by protocol */
    public static final long EADDRINUSE = 48; /* Address already in use */
    public static final long EADDRNOTAVAIL = 49; /* Cannot assign requested address */
    public static final long ENETDOWN = 50; /* Network is down */
    public static final long ENETUNREACH = 51; /* Network is unreachable */
    public static final long ENETRESET = 52; /* Network dropped connection because of reset */
    public static final long ECONNABORTED = 53; /* Software caused connection abort */
    public static final long ECONNRESET = 54; /* Connection reset by peer */
    public static final long ENOBUFS = 55; /* No buffer space available */
    public static final long EISCONN = 56; /* Transport endpoint is already connected */
    public static final long ENOTCONN = 57; /* Transport endpoint is not connected */
    public static final long ESHUTDOWN = 58; /* Cannot send after transport endpoint shutdown */
    public static final long ETOOMANYREFS = 59; /* Too many references: cannot splice */
    public static final long ETIMEDOUT = 60; /* Connection timed out */
    public static final long ECONNREFUSED = 61; /* Connection refused */
    public static final long ELOOP = 62; /* Too many symbolic links encountered */
    public static final long ENAMETOOLONG = 63; /* File name too long */
    public static final long EHOSTDOWN = 64; /* Host is down */
    public static final long EHOSTUNREACH = 65; /* No route to host */
    public static final long ENOTEMPTY = 66; /* Directory not empty */
    public static final long EUSERS = 68; /* Too many users */
    public static final long EDQUOT = 69; /* Quota exceeded */
    public static final long ESTALE = 70; /* Stale NFS file handle */
    public static final long EREMOTE = 71; /* Object is remote */
    public static final long ENOLCK = 77; /* No record locks available */
    public static final long ENOSYS = 78; /* Function not implemented */
    public static final long ENOMSG = 80; /* No message of desired type */
    public static final long EIDRM = 81; /* Identifier removed */
    public static final long ENOSR = 82; /* Out of streams resources */
    public static final long ETIME = 83; /* Timer expired */
    public static final long EBADMSG = 84; /* Not a data message */
    public static final long EPROTO = 85; /* Protocol error */
    public static final long ENODATA = 86; /* No data available */
    public static final long ENOSTR = 87; /* Device not a stream */
    public static final long ENOPKG = 92; /* Package not installed */
    public static final long EILSEQ = 116; /* Illegal byte sequence */
    /* The following are just random noise.. */
    public static final long ECHRNG = 88; /* Channel number out of range */
    public static final long EL2NSYNC = 89; /* Level 2 not synchronized */
    public static final long EL3HLT = 90; /* Level 3 halted */
    public static final long EL3RST = 91; /* Level 3 reset */
    public static final long ELNRNG = 93; /* Link number out of range */
    public static final long EUNATCH = 94; /* Protocol driver not attached */
    public static final long ENOCSI = 95; /* No CSI structure available */
    public static final long EL2HLT = 96; /* Level 2 halted */
    public static final long EBADE = 97; /* Invalid exchange */
    public static final long EBADR = 98; /* Invalid request descriptor */
    public static final long EXFULL = 99; /* Exchange full */
    public static final long ENOANO = 100; /* No anode */
    public static final long EBADRQC = 101; /* Invalid request code */
    public static final long EBADSLT = 102; /* Invalid slot */
    public static final long EDEADLOCK = EDEADLK;
    public static final long EBFONT = 104; /* Bad font file format */
    public static final long ENONET = 105; /* Machine is not on the network */
    public static final long ENOLINK = 106; /* Link has been severed */
    public static final long EADV = 107; /* Advertise error */
    public static final long ESRMNT = 108; /* Srmount error */
    public static final long ECOMM = 109; /* Communication error on send */
    public static final long EMULTIHOP = 110; /* Multihop attempted */
    public static final long EDOTDOT = 111; /* RFS specific error */
    public static final long EOVERFLOW = 112; /* Value too large for defined data type */
    public static final long ENOTUNIQ = 113; /* Name not unique on network */
    public static final long EBADFD = 114; /* File descriptor in bad state */
    public static final long EREMCHG = 115; /* Remote address changed */
    public static final long EUCLEAN = 117; /* Structure needs cleaning */
    public static final long ENOTNAM = 118; /* Not a XENIX named type file */
    public static final long ENAVAIL = 119; /* No XENIX semaphores available */
    public static final long EISNAM = 120; /* Is a named type file */
    public static final long EREMOTEIO = 121; /* Remote I/O error */
    public static final long ELIBACC = 122; /* Can not access a needed shared library */
    public static final long ELIBBAD = 123; /* Accessing a corrupted shared library */
    public static final long ELIBSCN = 124; /* .lib section in a.out corrupted */
    public static final long ELIBMAX = 125; /* Attempting to link in too many shared libraries */
    public static final long ELIBEXEC = 126; /* Cannot exec a shared library directly */
    public static final long ERESTART = 127; /* Interrupted system call should be restarted */
    public static final long ESTRPIPE = 128; /* Streams pipe error */
    public static final long ENOMEDIUM = 129; /* No medium found */
    public static final long EMEDIUMTYPE = 130; /* Wrong medium type */

}
