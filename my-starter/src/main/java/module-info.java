module pmb.my.starter {

    requires java.desktop;
    requires transitive org.apache.commons.lang3;
    requires transitive org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;

    exports pmb.my.starter.exception;
    exports pmb.my.starter.utils;

}