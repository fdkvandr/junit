package com.corp.junit;

import com.corp.junit.extension.GlobalExtension;
import com.corp.junit.extension.UserServiceParamResolver;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({UserServiceParamResolver.class, GlobalExtension.class})
public abstract class TestBase {

}
