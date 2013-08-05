#import "Main.h"
#import <JavaNativeFoundation/JavaNativeFoundation.h>
#import "AX.h" // generated from AX.java
#import <unistd.h>
#import <Quartz/Quartz.h>
#import <AppKit/AppKit.h>

struct AXNotificationCBInfo{
	jobject cb;
	JavaVM* jvm;
};


// AXUIElementCreateApplication
JNIEXPORT jlong JNICALL AX_NS(AXUIElementCreateApplication)(JNIEnv *env, jclass clazz, jlong pid){	
ENTERCOCOA;
	return (jlong) AXUIElementCreateApplication(pid);
EXITCOCOA;	
}

// AXUIElementGetPid
JNIEXPORT jlong JNICALL AX_NS(AXUIElementGetPid)(JNIEnv *env, jclass clazz, jlong axElement){
ENTERCOCOA;
	pid_t pid;
	AXUIElementGetPid((CFTypeRef)axElement, &pid);
	return pid;
EXITCOCOA;
}

// AXAPIEnabled
JNIEXPORT jboolean JNICALL AX_NS(AXAPIEnabled)(JNIEnv *env, jclass clazz){
ENTERCOCOA;
	return AXAPIEnabled();
EXITCOCOA;
}

// AXUIElementCreateSystemWide
JNIEXPORT jlong JNICALL AX_NS(AXUIElementCreateSystemWide)(JNIEnv *env, jclass clazz){
ENTERCOCOA;
	return (jlong) AXUIElementCreateSystemWide();
EXITCOCOA;
}

// AXUIElementCopyBooleanAttributeValue
JNIEXPORT jint JNICALL AX_NS(AXUIElementCopyBooleanAttributeValue)(JNIEnv *env, jclass clazz, jlong axElement, jstring attr){
ENTERCOCOA;
	NSString * nsAttr = JNFJavaToNSString(env, attr);
	CFBooleanRef result;
	int err = -1;
	if((err = AXUIElementCopyAttributeValue((CFTypeRef) axElement, (CFStringRef)nsAttr, (CFTypeRef*)&result)) != kAXErrorSuccess)
		return err;
	BOOL ret = CFBooleanGetValue(result);
	CFRelease(result);
	return ret ? 1 : 0;
EXITCOCOA;
}

//CFBooleanGetValue
JNIEXPORT jboolean JNICALL AX_NS(CFBooleanGetValue)(JNIEnv *env, jclass clazz, jlong cfTypeRef){
ENTERCOCOA;
	return CFBooleanGetValue((CFTypeRef)cfTypeRef) ? 1 : 0;
EXITCOCOA;
}

// AXUIElementCopyReferenceAttributeValue
JNIEXPORT jlong JNICALL AX_NS(AXUIElementCopyReferenceAttributeValue)(JNIEnv *env, jclass clazz, jlong axElement, jstring attr){
ENTERCOCOA;
	NSString * nsAttr = JNFJavaToNSString(env, attr);
	AXUIElementRef el = NULL;
	int err = -1;
	if((err = AXUIElementCopyAttributeValue((CFTypeRef) axElement, (CFStringRef)nsAttr, (CFTypeRef*)&el)) != kAXErrorSuccess)
		return err;
	return (jlong) el;
EXITCOCOA;
}

// AXUIElementCopyStringAttributeValue
JNIEXPORT jstring JNICALL AX_NS(AXUIElementCopyStringAttributeValue)(JNIEnv *env, jclass clazz, jlong axElement, jstring attr){
ENTERCOCOA;
@try{
	NSString * nsAttr = JNFJavaToNSString(env, attr);

	NSString* nsString;
	if(AXUIElementCopyAttributeValue((CFTypeRef)axElement, (CFStringRef)nsAttr, (CFTypeRef*)&nsString) != kAXErrorSuccess)
		return NULL;
	
	if(nsString == NULL)
		return NULL;

	jstring ret = JNFNSToJavaString(env, nsString);
	[nsString release];

	return ret;
} @catch(...){
    return NULL;
}
EXITCOCOA;
}


// AXUIElementCopyReferenceArrayAttributeValue
JNIEXPORT jlongArray JNICALL AX_NS(AXUIElementCopyReferenceArrayAttributeValue)(JNIEnv *env, jclass clazz, jlong axElement, jstring attr){
ENTERCOCOA;

	@try{
		NSString * nsAttr = JNFJavaToNSString(env, attr);

		NSArray* nsArray;
		if(AXUIElementCopyAttributeValue((CFTypeRef)axElement, (CFStringRef)nsAttr, (CFTypeRef*)&nsArray) != kAXErrorSuccess)
			return NULL;
	    
	    if(nsArray == NULL)
	        return NULL;
	    
	    int count = [nsArray count];
	    	    
	    long data[count]; 
	    
	    int i;
	    for(i = 0; i < count; i++){
	        AXUIElementRef ref = (AXUIElementRef)[nsArray objectAtIndex:i];
	        CFRetain(ref);
	        data[i] = (long) ref;
	    }
	    
	    jlongArray ret = (*env)->NewLongArray(env, count);
	    (*env)->SetLongArrayRegion(env, ret, (jsize)0, (jsize) count, (jlong*)data);
	    
	    [nsArray release];
	    
	    return ret;
	} @catch(...){
	    return NULL;
	}

EXITCOCOA
}


// AXObserverCallback
void AXOCallback(AXObserverRef observer, AXUIElementRef axElement, CFStringRef notification, void *refcon){
	
	struct AXNotificationCBInfo* cbinfo = (struct AXNotificationCBInfo*) refcon;
	JavaVM* jvm = cbinfo->jvm;
    JNIEnv *env;
    (*jvm)->AttachCurrentThread(jvm, (void **)&env, NULL);
	jobject cb = cbinfo->cb;
	
	jclass cls = (*env)->GetObjectClass(env, cb);
	jmethodID mid = (*env)->GetMethodID(env, cls, "callback", "(JLjava/lang/String;Ljava/lang/Object;)V");
	if (mid == 0)
		return;
	(*env)->CallVoidMethod(env, cb, mid, axElement, JNFNSToJavaString(env, (NSString*)notification), 0);
}

// AXObserverCreate
JNI_SIG(jlong, AX_NS(AXObserverCreate))(JNIEnv *env, jclass clazz, jlong pid){
ENTERCOCOA;
	AXObserverRef obs = NULL;
	AXError err = AXObserverCreate(pid, AXOCallback, &obs);
	if(err != kAXErrorSuccess)
		return err;
	return (jlong)obs;
EXITCOCOA;
}

// AXObserverGetRunLoopSource
JNI_SIG(jlong, AX_NS(AXObserverGetRunLoopSource))(JNIEnv *env, jclass clazz, jlong observer){
ENTERCOCOA;
	return (jlong)AXObserverGetRunLoopSource((AXObserverRef)observer);
EXITCOCOA;
}

// CFRunLoopGetCurrent
JNI_SIG(jlong, AX_NS(CFRunLoopGetCurrent))(JNIEnv *env, jclass clazz){
ENTERCOCOA;
	return (jlong)CFRunLoopGetCurrent();
EXITCOCOA;
}

// CFRunLoopGetMain
JNI_SIG(jlong, AX_NS(CFRunLoopGetMain))(JNIEnv *env, jclass clazz){
ENTERCOCOA;
	return (jlong)CFRunLoopGetMain();
EXITCOCOA;
}

// CFRunLoopRun
JNI_SIG(void, AX_NS(CFRunLoopRun))(JNIEnv *env, jclass clazz){
ENTERCOCOA;
	CFRunLoopRun();
EXITCOCOA;
}

// CFRunLoopAddSource
JNI_SIG(void, AX_NS(CFRunLoopAddSource))(JNIEnv *env, jclass clazz, jlong runLoop, jlong runLoopSource, jstring mode){
ENTERCOCOA;
	//NSString* sMode = JNFJavaToNSString(env, mode);
	CFRunLoopAddSource((CFRunLoopRef)runLoop, (CFRunLoopSourceRef)runLoopSource, kCFRunLoopCommonModes);
EXITCOCOA;
}

// AXObserverAddNotification
JNI_SIG(jlong, AX_NS(AXObserverAddNotification))(JNIEnv *env, jclass clazz, jlong observer, jlong axElement, jstring notification, jobject cb){
ENTERCOCOA;
	NSString* sNotification = JNFJavaToNSString(env, notification);
	struct AXNotificationCBInfo* cbinfo = (struct AXNotificationCBInfo*) malloc(sizeof(cbinfo));
	JavaVM* jvm;
	(*env)->GetJavaVM(env, &jvm);
	cbinfo->jvm = jvm;
	cbinfo->cb = (*env)->NewGlobalRef(env, cb); 
	return (jlong)AXObserverAddNotification((AXObserverRef)observer, (AXUIElementRef) axElement, (CFStringRef)JNFJavaToNSString(env, notification), (void*) cbinfo);
EXITCOCOA;
}

// CGContextSetLineCap
JNI_SIG(void, AX_NS(CGContextSetLineCap))(JNIEnv *env, jclass clazz, jlong context, jlong style){
ENTERCOCOA;
	CGContextSetLineCap((CGContextRef) context, style);
EXITCOCOA;
}

// launchTask
JNI_SIG(jlong, AX_NS(launchTask))(JNIEnv *env, jclass clazz, jstring cmd, jobjectArray args){
ENTERCOCOA;
    jlong ret = 0;
    NSString* sCmd = JNFJavaToNSString(env, cmd);
    int size = (*env)->GetArrayLength(env, args);
    NSString* nsArgs[size];
    
    int i = 0;
    for(i = 0; i < size; i++)
        nsArgs[i] = JNFJavaToNSString(env, (jstring)(*env)->GetObjectArrayElement(env, args, i));

    NSArray* finalArgs = [NSArray arrayWithObjects:nsArgs count:size];

    NSTask* task = [[NSTask alloc] init];
    [task setLaunchPath:sCmd];
    [task setArguments:finalArgs];
    [task setStandardError:[NSPipe pipe]];
    [task setStandardOutput:[NSPipe pipe]];
    [task setStandardInput:[NSPipe pipe]];
    //NSTask* task = [NSTask launchedTaskWithLaunchPath:sCmd arguments:finalArgs];
    [task launch];
    ret = [task processIdentifier];

    return ret;
EXITCOCOA;
}


// AXUIElementIsAttributeSettable
JNIEXPORT jint JNICALL AX_NS(AXUIElementIsAttributeSettable)(JNIEnv *env, jclass clazz, jlong axElement, jstring attribute){
ENTERCOCOA;

	Boolean result;
	int err = -1;
	if((err = AXUIElementIsAttributeSettable((CFTypeRef) axElement, (CFStringRef)JNFJavaToNSString(env, attribute), &result)) != kAXErrorSuccess)
		return err;
	return result ? 1 : 0;
EXITCOCOA;
}


// CFRelease
JNIEXPORT void JNICALL AX_NS(CFRelease)(JNIEnv *env, jclass clazz, jlong axElement){	
ENTERCOCOA;
	CFRelease((CFTypeRef)axElement);
EXITCOCOA;
}


// runningApplications
JNIEXPORT jlongArray JNICALL AX_NS(runningApplications)(JNIEnv *env, jclass clazz){		
ENTERCOCOA;
/*
	NSArray* apps =  [[NSWorkspace sharedWorkspace] runningApplications];

    if(apps == NULL)
        return NULL;

	long pids[[apps count]];
	
	int i = 0;
	for(NSRunningApplication* app in apps){
		pids[i] = [app processIdentifier];
		i++;
	}
	
	jlongArray ret = JNFNewLongArray(env, sizeof(pids) / sizeof(long));
	(*env)->SetLongArrayRegion(env, ret, (jsize)0, (jsize)sizeof(pids) / sizeof(long), (jlong*)pids);
	
	return ret;
	*/

	OSStatus status;
    ProcessSerialNumber currentProcessPSN = {kNoProcess, kNoProcess};

    int MAX_PIDS = 1000;
    long jpids[MAX_PIDS];
    int idx = 0;
    
    do{
        status = GetNextProcess(&currentProcessPSN);
    
        if (status == noErr){
        	pid_t pid;
        	GetProcessPID (&currentProcessPSN, &pid);
        	jpids[idx] = pid;
        	idx++;
        }
    } while (status == noErr && idx < MAX_PIDS);

    jlongArray ret = JNFNewLongArray(env, idx);
	(*env)->SetLongArrayRegion(env, ret, (jsize)0, (jsize)idx, (jlong*)jpids);
	
	return ret;

EXITCOCOA;	
}

// CFEqual
JNIEXPORT jboolean JNICALL AX_NS(CFEqual)(JNIEnv *env, jclass clazz, jlong axElement1, jlong axElement2){
ENTERCOCOA;
	return CFEqual((CFTypeRef) axElement1, (CFTypeRef) axElement2);
EXITCOCOA;
}

// CFHash
JNIEXPORT jlong JNICALL AX_NS(CFHash)(JNIEnv *env, jclass clazz, jlong axElement){
ENTERCOCOA;
	return (jlong) CFHash((CFTypeRef) axElement);
EXITCOCOA;
}

//CFRetain
JNIEXPORT jlong JNICALL AX_NS(CFRetain)(JNIEnv *env, jclass clazz, jlong axElement){
ENTERCOCOA;
	return (jlong) CFRetain((CFTypeRef) axElement);
EXITCOCOA;
}

// AXUIElementCopyElementAtPosition
JNIEXPORT jlong JNICALL AX_NS(AXUIElementCopyElementAtPosition)(JNIEnv *env, jclass clazz, jlong axParent, jfloat x, jfloat y){
ENTERCOCOA;
	AXUIElementRef el = NULL;
	int err = -1;
	if ((err = AXUIElementCopyElementAtPosition((CFTypeRef) axParent, x, y, &el)) != kAXErrorSuccess)
		return err;
	
	return (jlong) el;
EXITCOCOA;
}

// AXUIElementCopyAttributeNames
JNIEXPORT jobjectArray JNICALL AX_NS(AXUIElementCopyAttributeNames)(JNIEnv *env, jclass clazz, jlong axElement){	
ENTERCOCOA;		
	
	NSArray *attributes = NULL;
	if(AXUIElementCopyAttributeNames((CFTypeRef) axElement,(CFArrayRef*)&attributes) != kAXErrorSuccess )
		return NULL;
	
	
	jobjectArray ret = (jobjectArray)(*env)->NewObjectArray(env, [attributes count],
											   (*env)->FindClass(env, "java/lang/String"),
											   (*env)->NewStringUTF(env, ""));
	
	int i = 0;
	for(NSString* attributeName in attributes){
		(*env)->SetObjectArrayElement(env, ret, i, (*env)->NewStringUTF(env, [attributeName UTF8String]));
		i++;
	}

    [attributes release];
		
	return ret;
EXITCOCOA;
}


// getAppName
JNIEXPORT jstring JNICALL AX_NS(getAppName)(JNIEnv *env, jclass clazz, jlong pid){		
ENTERCOCOA;	

    NSRunningApplication* app = [NSRunningApplication runningApplicationWithProcessIdentifier:pid];

    if(app == NULL)
        return NULL;

    return JNFNSToJavaString(env, [app localizedName]);
EXITCOCOA;	
}

// terminated
JNIEXPORT jint JNICALL AX_NS(terminated)(JNIEnv *env, jclass clazz, jlong pid){
ENTERCOCOA;	

    NSRunningApplication* app = [NSRunningApplication runningApplicationWithProcessIdentifier:pid];

    if(app == NULL)
        return -1;

    return app.terminated ? 1 : 0;
EXITCOCOA;	
}

// finishedLaunching
JNIEXPORT jint JNICALL AX_NS(finishedLaunching)(JNIEnv *env, jclass clazz, jlong pid){
ENTERCOCOA;	

    NSRunningApplication* app = [NSRunningApplication runningApplicationWithProcessIdentifier:pid];

    if(app == NULL)
        return -1;

    return app.finishedLaunching ? 1 : 0;
EXITCOCOA;	
}

// hide
JNIEXPORT jint JNICALL AX_NS(hide)(JNIEnv *env, jclass clazz, jlong pid){
ENTERCOCOA;	
    NSRunningApplication* app = [NSRunningApplication runningApplicationWithProcessIdentifier:pid];

    if(app == NULL)
        return -1;

    return [app hide] ? 1 : 0;
EXITCOCOA;	
}

// activate
JNIEXPORT jint JNICALL AX_NS(activate)(JNIEnv *env, jclass clazz, jlong pid){
ENTERCOCOA;	
    NSRunningApplication* app = [NSRunningApplication runningApplicationWithProcessIdentifier:pid];

    if(app == NULL)
        return -1;

    return [app activateWithOptions:NSApplicationActivateIgnoringOtherApps] ? 1 : 0;
EXITCOCOA;	
}


// active
JNIEXPORT jint JNICALL AX_NS(active)(JNIEnv *env, jclass clazz, jlong pid){
ENTERCOCOA;	
    NSRunningApplication* app = [NSRunningApplication runningApplicationWithProcessIdentifier:pid];

    if(app == NULL)
        return -1;

    return app.active ? 1 : 0;
EXITCOCOA;	
}

// unhide
JNIEXPORT jint JNICALL AX_NS(unhide)(JNIEnv *env, jclass clazz, jlong pid){
ENTERCOCOA;	
    NSRunningApplication* app = [NSRunningApplication runningApplicationWithProcessIdentifier:pid];

    if(app == NULL)
        return -1;

    return [app unhide] ? 1 : 0;
EXITCOCOA;	
}

// terminate
JNIEXPORT jint JNICALL AX_NS(terminate)(JNIEnv *env, jclass clazz, jlong pid){
ENTERCOCOA;	
    NSRunningApplication* app = [NSRunningApplication runningApplicationWithProcessIdentifier:pid];

    if(app == NULL)
        return -1; 

    return [app terminate] ? 1 : 0;
EXITCOCOA;	
}

// forceTerminate
JNIEXPORT jint JNICALL AX_NS(forceTerminate)(JNIEnv *env, jclass clazz, jlong pid){
ENTERCOCOA;	
    NSRunningApplication* app = [NSRunningApplication runningApplicationWithProcessIdentifier:pid];

    if(app == NULL)
        return -1;

    return [app forceTerminate] ? 1 : 0;
EXITCOCOA;	
}

// hidden
JNIEXPORT jint JNICALL AX_NS(hidden)(JNIEnv *env, jclass clazz, jlong pid){
ENTERCOCOA;	
    NSRunningApplication* app = [NSRunningApplication runningApplicationWithProcessIdentifier:pid];

    if(app == NULL)
        return -1;

    return app.hidden ? 1 : 0;
EXITCOCOA;	
}

// getRect
JNIEXPORT jdoubleArray JNICALL AX_NS(getRect)(JNIEnv *env, jclass clazz, jlong axElement){
ENTERCOCOA;	
	
	NSRect rect;
	AXValueRef value;
	
	// get size
	int err;
	if ((err = AXUIElementCopyAttributeValue((CFTypeRef)axElement, kAXSizeAttribute, (CFTypeRef *) &value)) != kAXErrorSuccess)
		return NULL;
	AXValueGetValue(value, kAXValueCGSizeType, (void *) &rect.size);
	CFRelease(value);
	
	// get position
	if ((err = AXUIElementCopyAttributeValue((CFTypeRef)axElement, kAXPositionAttribute, (CFTypeRef *) &value)) != kAXErrorSuccess)
		return NULL;
	AXValueGetValue(value, kAXValueCGPointType, (void *) &rect.origin);
	CFRelease(value);
	
	double data[4];
	data[0] = rect.origin.x;
	data[1] = rect.origin.y;
	data[2] = rect.size.width;
	data[3] = rect.size.height;

	jdoubleArray ret = JNFNewLongArray(env, 4);
	(*env)->SetLongArrayRegion(env, ret, (jsize)0, (jsize) 4, (jlong*) data);
	return ret;
EXITCOCOA;	
}

//getSize
JNIEXPORT jdoubleArray JNICALL AX_NS(getSize)(JNIEnv *env, jclass clazz, jlong axElement){
ENTERCOCOA;	
	
	NSRect rect;
	AXValueRef value;
	
	// get size
	int err;
	if ((err = AXUIElementCopyAttributeValue((CFTypeRef)axElement, kAXSizeAttribute, (CFTypeRef *) &value)) != kAXErrorSuccess)
		return NULL;
	AXValueGetValue(value, kAXValueCGSizeType, (void *) &rect.size);
	CFRelease(value);
	
	double data[2];
	data[0] = rect.size.width;
	data[1] = rect.size.height;

	jdoubleArray ret = JNFNewLongArray(env, 2);
	(*env)->SetLongArrayRegion(env, ret, (jsize)0, (jsize) 2, (jlong*) data);
	return ret;
EXITCOCOA;	
}

//getPos
JNIEXPORT jdoubleArray JNICALL AX_NS(getPos)(JNIEnv *env, jclass clazz, jlong axElement){
ENTERCOCOA;	
	
	NSRect rect;
	AXValueRef value;
	
	// get position
	int err = -1;
	if ((err = AXUIElementCopyAttributeValue((CFTypeRef)axElement, kAXPositionAttribute, (CFTypeRef *) &value)) != kAXErrorSuccess)
		return NULL;
	AXValueGetValue(value, kAXValueCGPointType, (void *) &rect.origin);
	CFRelease(value);
	
	double data[2];
	data[0] = rect.origin.x;
	data[1] = rect.origin.y;

	jdoubleArray ret = JNFNewLongArray(env, 2);
	(*env)->SetLongArrayRegion(env, ret, (jsize)0, (jsize) 2, (jlong*) data);
	return ret;
EXITCOCOA;	
}

// runApplication
JNIEXPORT void JNICALL AX_NS(runApplication)(JNIEnv *env, jclass clazz){
ENTERCOCOA;	
    [NSApplication sharedApplication];
    [NSApp run];
EXITCOCOA;	
}

// createOverlayWindow
JNIEXPORT jlong JNICALL AX_NS(createOverlayWindow)(JNIEnv *env, jclass clazz){
ENTERCOCOA;	
    NSRect frame = NSMakeRect(0, 0, 0, 0);
    NSWindow* window  = [[NSWindow alloc] initWithContentRect:frame styleMask:NSBorderlessWindowMask backing:NSBackingStoreBuffered defer:YES];
    [window setBackgroundColor:[NSColor clearColor]];
    [window setOpaque:NO];
    [window setAlphaValue:1.0];
    [window setLevel:NSScreenSaverWindowLevel];
    [window setIgnoresMouseEvents:YES];
    [window setReleasedWhenClosed:YES];
    [window makeKeyAndOrderFront:NSApp];
    return (jlong)window;
EXITCOCOA;
}


// createCGContext
JNIEXPORT jlong JNICALL AX_NS(createCGContext)(JNIEnv *env, jclass clazz, jlong nsWindow){
ENTERCOCOA;
	NSWindow* wnd = (NSWindow*) nsWindow;
	return (jlong) CFRetain([[wnd graphicsContext] graphicsPort]);	
EXITCOCOA;	
}

// CFGetRetainCount
JNIEXPORT jlong JNICALL AX_NS(CFGetRetainCount)(JNIEnv *env, jclass clazz, jlong cfTypeRef){
ENTERCOCOA;
	return (jlong) CFGetRetainCount((CFTypeRef) cfTypeRef);	
EXITCOCOA;	
}

//CFGetTypeID
JNIEXPORT jlong JNICALL AX_NS(CFGetTypeID)(JNIEnv *env, jclass clazz, jlong cfTypeRef){
ENTERCOCOA;
	return (jlong) CFGetTypeID((CFTypeRef) cfTypeRef);	
EXITCOCOA;	
}

//AXUIElementGetTypeID
JNIEXPORT jlong JNICALL AX_NS(AXUIElementGetTypeID)(JNIEnv *env, jclass clazz){
ENTERCOCOA;
	return (jlong) AXUIElementGetTypeID();	
EXITCOCOA;	
}

//CFBooleanGetTypeID
JNIEXPORT jlong JNICALL AX_NS(CFBooleanGetTypeID)(JNIEnv *env, jclass clazz){
ENTERCOCOA;
	return (jlong) CFBooleanGetTypeID();	
EXITCOCOA;	
}

//CFArrayGetTypeID
JNIEXPORT jlong JNICALL AX_NS(CFArrayGetTypeID)(JNIEnv *env, jclass clazz){
ENTERCOCOA;
	return (jlong) CFArrayGetTypeID();	
EXITCOCOA;	
}

//CFStringGetTypeID
JNIEXPORT jlong JNICALL AX_NS(CFStringGetTypeID)(JNIEnv *env, jclass clazz){
ENTERCOCOA;
	return (jlong) CFStringGetTypeID();	
EXITCOCOA;	
}

//AXValueGetTypeID
JNIEXPORT jlong JNICALL AX_NS(AXValueGetTypeID)(JNIEnv *env, jclass clazz){
ENTERCOCOA;
	return (jlong) AXValueGetTypeID();	
EXITCOCOA;	
}

//CFStringGetCString
JNIEXPORT jstring JNICALL AX_NS(CFStringGetCString)(JNIEnv *env, jclass clazz, jlong cfStringRef){
ENTERCOCOA;
	return JNFNSToJavaString(env, (NSString*)cfStringRef);	
EXITCOCOA;	
}


// createConnectedScreens
JNIEXPORT jlongArray JNICALL AX_NS(createConnectedScreens)(JNIEnv *env, jclass clazz){
ENTERCOCOA;
	NSArray* nsScreens = [NSScreen screens];
	
    if(nsScreens == NULL)
        return NULL;

    int count = [nsScreens count];
    
	long screens[count];
	
	int i = 0;
	for(NSScreen* screen in nsScreens){
		screens[i] = (long) screen;
		CFRetain(screen);
		i++;
	}
	
	jlongArray ret = (*env)->NewLongArray(env, count);
	(*env)->SetLongArrayRegion(env, ret, (jsize)0, count, (jlong*)screens);
	return ret;
EXITCOCOA;	
}

// createMainScreen
JNIEXPORT jlong JNICALL AX_NS(createMainScreen)(JNIEnv *env, jclass clazz){
ENTERCOCOA;
	NSArray* nsScreens = [NSScreen screens];
	if(nsScreens == NULL)
		return 0;
	
	NSScreen* nsScreen = [nsScreens objectAtIndex:0];
	CFRetain(nsScreen);
	return (jlong) nsScreen;
EXITCOCOA;	
}

// getScreenFrame
JNIEXPORT jfloatArray JNICALL AX_NS(getScreenFrame)(JNIEnv *env, jclass clazz, jlong screen){
ENTERCOCOA;
	NSScreen* nsScreen = (NSScreen*) screen;
	
	NSRect frame = [nsScreen frame];
	
	float rect[4];
	rect[0] = frame.origin.x;
	rect[1] = frame.origin.y;
	rect[2] = frame.size.width;
	rect[3] = frame.size.height;
		
	jfloatArray ret = (*env)->NewFloatArray(env, 4);
	(*env)->SetFloatArrayRegion(env, ret, (jsize)0, 4, (jfloat*)rect);
	return ret;
EXITCOCOA;	
}


// CGContextFlush
JNIEXPORT void JNICALL AX_NS(CGContextFlush)(JNIEnv *env, jclass clazz, jlong context){
ENTERCOCOA;
	CGContextFlush((CGContextRef) context);
EXITCOCOA;	
}

// CGContextSetRGBStrokeColor
JNIEXPORT void JNICALL AX_NS(CGContextSetRGBStrokeColor)(JNIEnv *env, jclass clazz, jlong context, jfloat red, jfloat green, jfloat blue, jfloat alpha){
ENTERCOCOA;
	CGContextSetRGBStrokeColor((CGContextRef) context, (CGFloat) red, (CGFloat) green, (CGFloat) blue, (CGFloat) alpha);
EXITCOCOA;	
}

// CGContextSetRGBFillColor
JNIEXPORT void JNICALL AX_NS(CGContextSetRGBFillColor)(JNIEnv *env, jclass clazz, jlong context, jfloat red, jfloat green, jfloat blue, jfloat alpha){
ENTERCOCOA;
	CGContextSetRGBFillColor((CGContextRef) context, (CGFloat) red, (CGFloat) green, (CGFloat) blue, (CGFloat) alpha);
EXITCOCOA;	
}

// CGContextSetLineWidth
JNIEXPORT void JNICALL AX_NS(CGContextSetLineWidth)(JNIEnv *env, jclass clazz, jlong context, jfloat width){
ENTERCOCOA;
	CGContextSetLineWidth((CGContextRef) context, (CGFloat) width);
EXITCOCOA;	
}

// CGContextStrokeRect
JNIEXPORT void JNICALL AX_NS(CGContextStrokeRect)(JNIEnv *env, jclass clazz, jlong context, jfloat x, jfloat y, jfloat width, jfloat height){
ENTERCOCOA;
	CGContextRef c = (CGContextRef) context;
	CGContextStrokeRect(c, CGRectMake((CGFloat)x, (CGFloat)y, (CGFloat)width, (CGFloat)height));
EXITCOCOA;	
}

// CGContextStrokeLine
JNIEXPORT void JNICALL AX_NS(CGContextStrokeLine)(JNIEnv *env, jclass clazz, jlong context, jfloat x1, jfloat y1, jfloat x2, jfloat y2){
ENTERCOCOA;
	CGContextRef c = (CGContextRef) context;
	CGPoint points[] = { CGPointMake(x1, y1), CGPointMake(x2, y2) };
	CGContextStrokeLineSegments(c, points, 2);
EXITCOCOA;	
}

// CGContextClearRect
JNIEXPORT void JNICALL AX_NS(CGContextClearRect)(JNIEnv *env, jclass clazz, jlong context, jfloat x, jfloat y, jfloat width, jfloat height){
ENTERCOCOA;
	CGContextRef c = (CGContextRef) context;
	CGContextClearRect(c, CGRectMake((CGFloat)x, (CGFloat)y, (CGFloat)width, (CGFloat)height));
EXITCOCOA;	
}

// CGContextFillRect
JNIEXPORT void JNICALL AX_NS(CGContextFillRect)(JNIEnv *env, jclass clazz, jlong context, jfloat x, jfloat y, jfloat width, jfloat height){
ENTERCOCOA;
	CGContextRef c = (CGContextRef) context;
	CGContextFillRect(c, CGRectMake((CGFloat)x, (CGFloat)y, (CGFloat)width, (CGFloat)height));
EXITCOCOA;	
}

// CGContextFillEllipseInRect
JNIEXPORT void JNICALL AX_NS(CGContextFillEllipseInRect)(JNIEnv *env, jclass clazz, jlong context, jfloat x, jfloat y, jfloat width, jfloat height){
ENTERCOCOA;
	CGContextRef c = (CGContextRef) context;
	CGContextFillEllipseInRect(c, CGRectMake((CGFloat)x, (CGFloat)y, (CGFloat)width, (CGFloat)height));
EXITCOCOA;	
}

// CGContextStrokeEllipseInRect
JNIEXPORT void JNICALL AX_NS(CGContextStrokeEllipseInRect)(JNIEnv *env, jclass clazz, jlong context, jfloat x, jfloat y, jfloat width, jfloat height){
ENTERCOCOA;
	CGContextRef c = (CGContextRef) context;
	CGContextStrokeEllipseInRect(c, CGRectMake((CGFloat)x, (CGFloat)y, (CGFloat)width, (CGFloat)height));
EXITCOCOA;	
}

// CGContextSelectFont
JNIEXPORT void JNICALL AX_NS(CGContextSelectFont)(JNIEnv *env, jclass clazz, jlong context, jstring fontName, jfloat size, jint encoding){
ENTERCOCOA;
	jboolean iscopy;
	const char *sFontName = (*env)->GetStringUTFChars(env, fontName, &iscopy);
	CGContextSelectFont((CGContextRef) context, sFontName, (CGFloat) size, kCGEncodingMacRoman);
	(*env)->ReleaseStringUTFChars(env, fontName, sFontName);
EXITCOCOA;	
}

// CGContextShowTextAtPoint
JNIEXPORT void JNICALL AX_NS(CGContextShowTextAtPoint)(JNIEnv *env, jclass clazz, jlong context, jfloat x, jfloat y, jstring text){
ENTERCOCOA;
	jboolean iscopy;
	const char *sText = (*env)->GetStringUTFChars(env, text, &iscopy);
	int length = (*env)->GetStringLength(env, text);
	CGContextRef c = (CGContextRef) context;
	CGContextShowTextAtPoint(c, x, y, sText, length);
	(*env)->ReleaseStringUTFChars(env, text, sText);
EXITCOCOA;	
}

// setWindowFrame
JNIEXPORT void JNICALL AX_NS(setWindowFrame)(JNIEnv *env, jclass clazz, jlong window, jfloat x, jfloat y, jfloat width, jfloat height){
ENTERCOCOA;	
    NSRect frame = NSMakeRect(x, y, width, height);
    NSWindow* nsWindow = (NSWindow*) window;
    [nsWindow setFrame:frame display:YES];
EXITCOCOA;	
}

// closeWindow
JNIEXPORT void JNICALL AX_NS(closeWindow)(JNIEnv *env, jclass clazz, jlong window){
ENTERCOCOA;	
    NSWindow* nsWindow = (NSWindow*) window;
    [nsWindow close];
EXITCOCOA;	
}

//AXValueGetType
JNIEXPORT jlong JNICALL AX_NS(AXValueGetType)(JNIEnv *env, jclass clazz, jlong axValueRef){
ENTERCOCOA;
	return AXValueGetType((AXValueRef)axValueRef);
EXITCOCOA;	
}

// AXUIElementCopyMultipleAttributeValues
JNIEXPORT jlongArray JNICALL AX_NS(AXUIElementCopyMultipleAttributeValues)(JNIEnv *env, jclass clazz, jlong axElement, jobjectArray attributes){
ENTERCOCOA;	

	int size = (*env)->GetArrayLength(env, attributes);
	NSString* nsAttributes[size];

	int i = 0;
	for(i = 0; i < size; i++)
		nsAttributes[i] = JNFJavaToNSString(env, (jstring)(*env)->GetObjectArrayElement(env, attributes, i));
	//nsAttributes[size - 1] = nil;
	
	NSArray* finalAttributes = [NSArray arrayWithObjects:nsAttributes count:size];
	
	NSArray* values;
	AXError err = AXUIElementCopyMultipleAttributeValues((CFTypeRef)axElement, (CFArrayRef) finalAttributes, 0, (CFArrayRef*)&values);

	if(err < 0 || values == NULL)
		return NULL;
	
	// copy references into a long array
    int count = [values count];
    long data[count]; 
    
    for(i = 0; i < count; i++){
        CFTypeRef ref = (CFTypeRef)[values objectAtIndex:i];
        CFRetain(ref);
        data[i] = (long) ref;
    }
    
    jlongArray ret = (*env)->NewLongArray(env, count);
    (*env)->SetLongArrayRegion(env, ret, (jsize)0, (jsize) count, (jlong*)data);
    
    [values release];
    
    return ret;
EXITCOCOA;	
}

// kill
JNIEXPORT jlong JNICALL AX_NS(kill)(JNIEnv *env, jclass clazz, jlong pid, jlong signal){
ENTERCOCOA;
    return (jlong) kill(pid, (int)signal);
EXITCOCOA;	
}

// execve
JNIEXPORT jlong JNICALL AX_NS(execve)(JNIEnv *env, jclass clazz, jstring cmd, jobjectArray args, jobjectArray envv, jlongArray fds){
ENTERCOCOA;
    
	jboolean iscopy;
    const char *cCmd = (*env)->GetStringUTFChars(env, cmd, &iscopy);

    size_t argsSize = (*env)->GetArrayLength(env, args);
    size_t envvSize = (*env)->GetArrayLength(env, envv);

    char* cArgs[argsSize + 1];
    int i = 0;
    for(i = 0; i < argsSize; i++)
        cArgs[i] = (char*)(*env)->GetStringUTFChars(env, (jstring)(*env)->GetObjectArrayElement(env, args, i), &iscopy);
    cArgs[argsSize] = 0;
    
    char* cEnvv[envvSize + 1];
    for(i = 0; i < envvSize; i++)
        cEnvv[i] = (char*)(*env)->GetStringUTFChars(env, (jstring)(*env)->GetObjectArrayElement(env, envv, i), &iscopy);
    cEnvv[envvSize] = 0;

    jlong cFds[6];
    (*env)->GetLongArrayRegion(env, fds, 0, 6, (jlong *)cFds);
        
    int pid = fork();
    if(pid == 0){
    	
    	// set up pipes
    	//stdin
    	if(cFds[0] != STDIN_FILENO){
    		dup2(cFds[0], STDIN_FILENO);
    	    close(cFds[1]);
    	}
    	//stdout
    	if(cFds[3] != STDOUT_FILENO){
    		dup2(cFds[3], STDOUT_FILENO);
    	    close(cFds[2]);
    	}
    	//stderr
    	if(cFds[5] != STDERR_FILENO){
    		dup2(cFds[5], STDERR_FILENO);
    	    close(cFds[4]);
    	}
    	    		
    	// run process
        int ret = execve(cCmd, cArgs, cEnvv);
        if(ret < 0){
        	exit(EXIT_FAILURE);
        }
        exit(EXIT_SUCCESS);
    }

    (*env)->ReleaseStringUTFChars(env, cmd, cCmd);

    for(i = 0; i < argsSize; i++)
    	(*env)->ReleaseStringUTFChars(env, (jstring)(*env)->GetObjectArrayElement(env, args, i), cArgs[i]);
    for(i = 0; i < envvSize; i++)
    	(*env)->ReleaseStringUTFChars(env, (jstring)(*env)->GetObjectArrayElement(env, envv, i), cEnvv[i]);

    return (jlong) pid;
EXITCOCOA;
}

// open
JNIEXPORT jlong JNICALL AX_NS(open)(JNIEnv *env, jclass clazz, jstring path, jlong flags, jlong mode){
ENTERCOCOA;	
	jboolean iscopy;
	const char *sPath = (*env)->GetStringUTFChars(env, path, &iscopy);
	jlong ret = (jlong) open(sPath, flags, mode);
	(*env)->ReleaseStringUTFChars(env, path, sPath);
    return ret;
EXITCOCOA;	
}

// write
JNIEXPORT jlong JNICALL AX_NS(write)(JNIEnv *env, jclass clazz, jlong handle, jbyteArray data){
ENTERCOCOA;
	size_t size = (*env)->GetArrayLength(env, data);
	char* cData = malloc(sizeof(char) * size);
    (*env)->GetByteArrayRegion(env, data, 0, size, (jbyte *)cData);
    jlong ret = (jlong) write(handle, cData, size);
    free(cData);
    return ret;
EXITCOCOA;	
}

// errno
JNIEXPORT jlong JNICALL AX_NS(errno)(JNIEnv *env, jclass clazz){
ENTERCOCOA;
    return (jlong) errno;
EXITCOCOA;	
}

// strerror
JNIEXPORT jstring JNICALL AX_NS(strerror)(JNIEnv *env, jclass clazz, jlong err){
ENTERCOCOA;
	char* cStr = strerror(err);
	return (*env)->NewStringUTF(env, cStr);
EXITCOCOA;	
}

// pipe
JNIEXPORT jlongArray JNICALL AX_NS(pipe)(JNIEnv *env, jclass clazz){
ENTERCOCOA;
	int pipes[2];
	if(pipe(pipes) < 0)
		return NULL;
	
	jlong jlPipes[2];
	jlPipes[0] = pipes[0];
	jlPipes[1] = pipes[1];
		
	jlongArray ret = (*env)->NewLongArray(env, 2);
    (*env)->SetLongArrayRegion(env, ret, (jsize)0, (jsize) 2, jlPipes);
    
    return ret;	
EXITCOCOA;	
}

// close
JNIEXPORT jlong JNICALL AX_NS(close)(JNIEnv *env, jclass clazz, jlong fd){
ENTERCOCOA;
	return (jlong) close(fd);
EXITCOCOA;	
}

// read
JNIEXPORT jbyteArray JNICALL AX_NS(read)(JNIEnv *env, jclass clazz, jlong fd, jint numBytes){
ENTERCOCOA;
	void* buf = malloc(sizeof(char) * numBytes);
	int size = read(fd, buf, numBytes);
	if(size < 0)
		return NULL;
	
	jbyteArray ret = (*env)->NewByteArray(env, size);
    (*env)->SetByteArrayRegion(env, ret, (jsize)0, (jsize) size, (jbyte*)buf);
	free(buf);
	
	return ret;
EXITCOCOA;	
}

// waitpid
JNIEXPORT jlongArray JNICALL AX_NS(waitpid)(JNIEnv *env, jclass clazz, jlong pid, jlong options){
ENTERCOCOA;
	jlong res[2];
	int status;
	res[0] = waitpid(pid, &status, (int)options);
	res[1] = status;
	
    jlongArray ret = (*env)->NewLongArray(env, 2);
    (*env)->SetLongArrayRegion(env, ret, (jsize)0, (jsize) 2, res);

	return ret;
EXITCOCOA;	
}

// WIFEXITED
JNIEXPORT jboolean JNICALL AX_NS(WIFEXITED)(JNIEnv *env, jclass clazz, jlong status){
ENTERCOCOA;
	return WIFEXITED(status);
EXITCOCOA;	
}

// WEXITSTATUS
JNIEXPORT jlong JNICALL AX_NS(WEXITSTATUS)(JNIEnv *env, jclass clazz, jlong status){
ENTERCOCOA;
	return WEXITSTATUS(status);
EXITCOCOA;	
}

// WIFSIGNALED
JNIEXPORT jboolean JNICALL AX_NS(WIFSIGNALED)(JNIEnv *env, jclass clazz, jlong status){
ENTERCOCOA;
	return WIFSIGNALED(status);
EXITCOCOA;	
}

// WTERMSIG
JNIEXPORT jlong JNICALL AX_NS(WTERMSIG)(JNIEnv *env, jclass clazz, jlong status){
ENTERCOCOA;
	return WTERMSIG(status);
EXITCOCOA;	
}

// WCOREDUMP
JNIEXPORT jboolean JNICALL AX_NS(WCOREDUMP)(JNIEnv *env, jclass clazz, jlong status){
ENTERCOCOA;
	return WCOREDUMP(status);
EXITCOCOA;	
}

// WIFSTOPPED
JNIEXPORT jboolean JNICALL AX_NS(WIFSTOPPED)(JNIEnv *env, jclass clazz, jlong status){
ENTERCOCOA;
	return WIFSTOPPED(status);
EXITCOCOA;	
}

// WSTOPSIG
JNIEXPORT jlong JNICALL AX_NS(WSTOPSIG)(JNIEnv *env, jclass clazz, jlong status){
ENTERCOCOA;
	return WSTOPSIG(status);
EXITCOCOA;	
}

// WIFCONTINUED
JNIEXPORT jboolean JNICALL AX_NS(WIFCONTINUED)(JNIEnv *env, jclass clazz, jlong status){
ENTERCOCOA;
	return WIFCONTINUED(status);
EXITCOCOA;	
}

// fcntl
JNIEXPORT jlong JNICALL AX_NS(fcntl)(JNIEnv *env, jclass clazz, jlong fd, jlong cmd, jlong arg){
ENTERCOCOA;
		return fcntl((int)fd, (int)cmd, arg);
EXITCOCOA;	
}


/*  Attributes 
 AXValue
 AXDescription
 
 // The current mouse position with origin at top right.
 NSPoint cocoaPoint = [NSEvent mouseLocation];
 
 AXUIElementCopyMultipleAttributeValues   <========== for SPEEDUP??????
 */
