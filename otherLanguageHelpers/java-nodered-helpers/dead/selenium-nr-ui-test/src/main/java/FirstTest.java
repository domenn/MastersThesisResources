import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

/**
 * @author Domen Mori 9. 08. 2018.
 */
public class FirstTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Logger lg = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

//        System.setProperty("webdriver.chrome.driver", "C:\\Downloads\\chromedriver.exe");
//
//        System.setProperty("webdriver.chrome.driver", "/Users/Niranjan/My Briefcase/My Development/eclipse_learning/SeleniumLearning/lib/webdrivers/chromedriver");

        ChromeDriver driver = new ChromeDriver();
        driver.get("http://127.0.0.1:1880/");
        WebElement elem1 = driver.findElementByClassName("58a6f229.4f268c");
        lg.warning(elem1.getText());
        elem1.click();
    }

}
