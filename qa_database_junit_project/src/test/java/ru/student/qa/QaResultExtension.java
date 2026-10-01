package ru.student.qa;

import org.junit.jupiter.api.extension.*;
import java.util.concurrent.atomic.AtomicInteger;

public class QaResultExtension implements BeforeAllCallback, AfterAllCallback, TestWatcher {
    private static final AtomicInteger passed=new AtomicInteger(), failed=new AtomicInteger(), skipped=new AtomicInteger();
    private static long start;
    public void beforeAll(ExtensionContext c){ passed.set(0);failed.set(0);skipped.set(0);start=System.currentTimeMillis();ResultDatabase.initialize(); }
    public void testSuccessful(ExtensionContext c){passed.incrementAndGet();}
    public void testFailed(ExtensionContext c,Throwable t){failed.incrementAndGet();}
    public void testDisabled(ExtensionContext c,java.util.Optional<String> r){skipped.incrementAndGet();}
    public void afterAll(ExtensionContext c){
        long ms=System.currentTimeMillis()-start; int total=passed.get()+failed.get()+skipped.get();
        long id=ResultDatabase.insertRun(DatabaseParameterizedTest.TEST_SET,total,passed.get(),failed.get(),ms);
        System.out.printf("QA result saved: run_id=%d total=%d passed=%d failed=%d time=%dms%n",id,total,passed.get(),failed.get(),ms);
    }
}
