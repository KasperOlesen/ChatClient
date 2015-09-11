/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Preben
 */
public class EchoServerTest {

    public EchoServerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of syntaxCheck method, of class EchoServer.
     */
    @Test
    public void testSyntaxCheck() {
        System.out.println("syntaxCheck");
        String msg = "MSG#BOB#Hej";
        EchoServer instance = new EchoServer();
        boolean expResult = true;
        boolean result = instance.syntaxCheck(msg);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testSyntaxCheckForHashSign() {
        System.out.println("HashSign");
        String msg = "MSGBOBHej";
        EchoServer instance = new EchoServer();
        boolean expResult = false;
        boolean result = instance.syntaxCheck(msg);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testSyntaxCheckForMsgLength() {
        System.out.println("MsgLength");
        String msg = "MSG#";
        EchoServer instance = new EchoServer();
        boolean expResult = false;
        boolean result = instance.syntaxCheck(msg);
        assertEquals(expResult, result);
    }
     @Test
    public void testSyntaxCheckForSpelling() {
        System.out.println("Spelling");
        String msg = "SGM#BOB#Test";
        EchoServer instance = new EchoServer();
        boolean expResult = false;
        boolean result = instance.syntaxCheck(msg);
        assertEquals(expResult, result);
    }
    
    

}
