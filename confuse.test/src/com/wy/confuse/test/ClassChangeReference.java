package com.wy.confuse.test;

public class ClassChangeReference {

    private ClassChange classChange;

    public void testInvoke(ClassChange classChange2) {
        classChange2.test();
        classChange2.count++;
        ClassChange classChange1 = new ClassChange();
        classChange1.test();
        classChange1.count++;
        classChange.count++;
        System.out.println(classChange.count);
        classChange.test();
    }

}
