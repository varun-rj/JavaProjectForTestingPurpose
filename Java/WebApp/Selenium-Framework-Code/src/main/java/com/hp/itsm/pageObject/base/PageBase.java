package com.hp.itsm.pageObject.base;

import com.hp.itsm.seleniumHelper.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class PageBase {

    private static final Logger LOGGER = LogManager.getLogger(PageBase.class);

    @Autowired
    protected SeleniumHelper seleniumHelper;
    @Autowired
    protected Wait wait;
    @Autowired
    protected Frames frames;
    @Autowired
    protected Window window;
    @Autowired
    protected DropDown dropDown;


}
