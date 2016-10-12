package no.westerdals.pg5100.frontend.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

public abstract class SeleniumTestBase {

    private static WebDriver driver;

    private static boolean tryToSetGeckoIfExists(String property, Path path){
        if(Files.exists(path)){
            System.setProperty(property, path.toAbsolutePath().toString());
            return true;
        }
        return false;
    }

    private static void setupDriverExecutable(String executableName, String property){
        String homeDir = System.getProperty("user.home");

        //first try Linux/Mac executable
        if(! tryToSetGeckoIfExists(property, Paths.get(homeDir,executableName))){
            //then check if on Windows
            if(! tryToSetGeckoIfExists(property, Paths.get(homeDir,executableName+".exe"))){
                fail("Cannot locate the "+executableName+" in your home directory "+homeDir);
            }
        }
    }

    private static WebDriver getFirefoxDriver(){
        /*
            Need to have an updated Firefox (eg 48.x), but also need
            to download and put the geckodriver (eg 0.10.0) in your own home dir.
            See:

            https://developer.mozilla.org/en-US/docs/Mozilla/QA/Marionette/WebDriver
            https://github.com/mozilla/geckodriver/releases

            However, it looks like this driver is still unstable, at least on Mac.
            Note: there was a big change in Firefox 47, which at the time completely
            broke the firefox driver, and the new "marionette" became the new one
         */

        setupDriverExecutable("geckodriver", "webdriver.gecko.driver");

        DesiredCapabilities desiredCapabilities = DesiredCapabilities.firefox();
        desiredCapabilities.setCapability("marionette", true);
        desiredCapabilities.setJavascriptEnabled(true);

        return  new FirefoxDriver(desiredCapabilities);
    }

    private static WebDriver getChromeDriver(){

        /*
            Need to have Chrome (eg version 53.x) and the Chrome Driver (eg 2.24),
            whose executable should be saved directly under your home directory

            see https://sites.google.com/a/chromium.org/chromedriver/getting-started
         */

        setupDriverExecutable("chromedriver", "webdriver.chrome.driver");

        return new ChromeDriver();
    }

    @BeforeClass
    public static void init() throws InterruptedException {

        //driver = getFirefoxDriver(); //need Selenium 3.x, but still giving few issues (at least on Mac)
        driver = getChromeDriver();


        /*
            When the integration tests in this class are run, it might be
            that WildFly is not ready yet, although it was started. So
            we need to wait till it is ready.
         */
        for(int i=0; i<30; i++){
            boolean ready = JBossUtil.isJBossUpAndRunning();
            if(!ready){
                Thread.sleep(1_000); //check every second
                continue;
            } else {
                break;
            }
        }
    }

    protected WebDriver getDriver(){
        return driver;
    }

    @AfterClass
    public static void tearDown(){
        driver.close();
    }


    @Before
    public void checkIfWildflyIsRunning(){

        //if for any reason WildFly is not running any more, do not fail the tests
        assumeTrue("Wildfly is not up and running", JBossUtil.isJBossUpAndRunning());
    }
}
