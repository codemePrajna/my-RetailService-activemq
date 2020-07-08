package com.common.util;

import org.springframework.stereotype.Component;

@Component
public class SharedObject {
    public static volatile Object sharedObj = new Object();
    public static volatile Object updateObj = new Object();
}
