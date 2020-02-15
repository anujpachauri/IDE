package com.anuj.execute;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ByteUtilsTest {

	@Test
	public void test() {
		
		byte[] b=ByteUtils.string2Byte("a");
		System.out.println(Arrays.toString(b));
		int num=ByteUtils.byte2Int(b, 0, b.length);
		System.out.println(num);
		b=ByteUtils.int2Byte(num, b.length);
		System.out.println(Arrays.toString(b));
	}
	
}
