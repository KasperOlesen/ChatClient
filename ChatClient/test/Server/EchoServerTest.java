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
    public void testSyntaxCheckTrue() {
        System.out.println("SyntaxCheckTrue");
        String msg = "MSG#BOB#Hej";
        EchoServer instance = new EchoServer();
        boolean expResult = true;
        boolean result = instance.syntaxCheck(msg);
        assertEquals(expResult, result);
    }

    @Test
    public void testSyntaxCheckForHashSign() {
        System.out.println("SyntaxCheckForHashSign");
        String msg = "MSGBOBHej";
        EchoServer instance = new EchoServer();
        boolean expResult = false;
        boolean result = instance.syntaxCheck(msg);
        assertEquals(expResult, result);
    }

    @Test
    public void testSyntaxCheckForMsgLength() {
        System.out.println("SyntaxCheckForMsgLength");
        String msg = "MSG#";
        EchoServer instance = new EchoServer();
        boolean expResult = false;
        boolean result = instance.syntaxCheck(msg);
        assertEquals(expResult, result);
    }

    @Test
    public void testSyntaxCheckForSpelling() {
        System.out.println("SyntaxCheckForSpelling");
        String msg = "SGM#BOB#Test";
        EchoServer instance = new EchoServer();
        boolean expResult = false;
        boolean result = instance.syntaxCheck(msg);
        assertEquals(expResult, result);
    }

    @Test
    public void testCheckUserNameTrue() {
        System.out.println("CheckUserNameTrue");
        String msg = "USER#BOB";
        EchoServer instance = new EchoServer();
        boolean expResult = true;
        boolean result = instance.checkUserName(msg);
        assertEquals(expResult, result);

    }
    
    @Test
    public void testCheckUserNameHashSign() {
        System.out.println("CheckUserNameHashSign");
        String msg = "USERBOB";
        EchoServer instance = new EchoServer();
        boolean expResult = false;
        boolean result = instance.checkUserName(msg);
        assertEquals(expResult, result);

    }
    
    @Test
    public void testCheckUserNameLength() {
        System.out.println("CheckUserNameLength");
        String msg = "USER#";
        EchoServer instance = new EchoServer();
        boolean expResult = false;
        boolean result = instance.checkUserName(msg);
        assertEquals(expResult, result);

    }

}
