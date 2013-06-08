package com.seovic.coherence;

import com.seovic.pof.PortableTypeSerializer;
import com.tangosol.io.pof.SimplePofContext;
import com.tangosol.util.Binary;
import com.tangosol.util.ExternalizableHelper;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import test.Person;

public class PortableTypeMojoTest {

    @Test @Ignore
    public void testInstrumentation() throws Exception {
        SimplePofContext ctx = new SimplePofContext();
        ctx.registerUserType(1, Person.class, new PortableTypeSerializer(1, Person.class));
        ctx.registerUserType(2, Person.Address.class, new PortableTypeSerializer(2, Person.Address.class));

        Person p = new Person("Homer", "Simpson", 50);
        p.setAddress(new Person.Address("123 Main St", "Springfield", "USA"));
        System.out.println(p);

        Binary bin = ExternalizableHelper.toBinary(p, ctx);
        Person p2 = (Person) ExternalizableHelper.fromBinary(bin, ctx);
        System.out.println(p2);

        assertEquals(p, p2);
    }
}
