//
//  NativeAddressBook.h
//  Untitled
//
//  Created by Sebastian Bauersfeld on 1/13/12.
//  Copyright (c) 2012, HU-Berlin. All rights reserved.
//

#import <Cocoa/Cocoa.h>

#define AX_NS(method) Java_org_fruit_alayer_macosx_AX_##method
#define JNI_SIG(ret, method) JNIEXPORT ret JNICALL method
#define ENTERCOCOA JNF_COCOA_ENTER(env)
#define EXITCOCOA JNF_COCOA_EXIT(env)
