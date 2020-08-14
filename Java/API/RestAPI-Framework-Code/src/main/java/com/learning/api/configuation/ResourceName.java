package com.learning.api.configuation;

public enum ResourceName {

    DEV_ENV("dev_env_config"),
    TEST_ENV("test_env_config");

    private String name;

    ResourceName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
