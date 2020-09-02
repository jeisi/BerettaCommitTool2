/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xrea.jeisi.berettacommittool2.xmlwriter;

/**
 *
 * @author jeisi
 */
public class XmlWriter {

    private static int tabWidth = 0;
    private static String lastStartTag = null;
    private static boolean isEnabled = true;

    public static void writeStartMethod(String name) {
        writePreStartTag();
        StringBuilder builder = new StringBuilder();
        builder.append(getTabSpace());
        builder.append("<method name='");
        builder.append(name);
        writePrintf(builder.toString());

        lastStartTag = "method";

        Indent();
    }

    public static void writeStartMethod(String fmt, Object... args) {
        writeStartMethod(String.format(fmt, args));
    }

    public static void writeEndMethod() {
        writeEndTag("method");
    }

    public static void writeEndMethodWithReturn() {
        writeReturnWithLine((new Throwable()).getStackTrace()[1].getLineNumber());
        writeEndMethod();
    }

    public static void writeEndMethodWithReturnValue(Object object) {
        writeReturnValueWithLine(object, (new Throwable()).getStackTrace()[1].getLineNumber());
        writeEndMethod();
    }

    public static void writeReturnWithLine(int line) {
        writePreStartTag();
        writePrintf("%s<return line='%d' />\n", getTabSpace(), line);
    }

    public static void writeReturnValueWithLine(Object object, int line) {
        writePreStartTag();
        writePrintf("%s<return line='%d'>%s</return>\n", getTabSpace(), line, object.toString());
    }

    public static void writeObject(String name, Object object) {
        writePreStartTag();
        String text = String.format("%s=%s", name, object.toString());
        writePrintf("%s<object>%s</object>\n", getTabSpace(), text);
    }

    public static void writeStatement(String statement) {
        writePreStartTag();
        writePrintf("%s<statement>%s</statement>\n", getTabSpace(), statement);
    }

    private static void writeEndTag(String tagName) {
        writeEndTag(tagName, /*comment=*/ "");
    }

    private static void writePreStartTag() {
        if (lastStartTag != null) {
            writePrintf("'>\n");
            lastStartTag = null;
        }
    }

    private static void writeEndTag(String tagName, String comment) {
        if (lastStartTag != null) {
            if (tagName.equals(lastStartTag)) {
                // タグの中身は空だった。
                Outdent();
                writePrintf("' />\n");
                lastStartTag = null;
                return;
            } else {
                throw new AssertionError("start tag と end tag が違っていたら駄目");
            }
        }

        Outdent();
        writePrintf("%s</%s>%s\n", getTabSpace(), tagName, comment);
    }

    private static void Indent() {
        tabWidth += 2;
    }

    private static void Outdent() {
        tabWidth -= 2;

        if (tabWidth < 0) {
            throw new AssertionError("tabWidth の値が負になりました。");
        }
    }

    private static String getTabSpace() {
        final String tabSpaceAll = "                                    ";
        return tabSpaceAll.substring(0, tabWidth);
    }

    private static void writePrintf(String fmt, Object... args) {
        if (!isEnabled) {
            return;
        }
        System.out.print(String.format(fmt, args));
    }

    private static void writePrintf(String text) {
        if (!isEnabled) {
            return;
        }
        System.out.print(text);
    }
}
