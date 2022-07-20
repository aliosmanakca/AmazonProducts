package stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.poi.ss.usermodel.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AmazonStepDef {

    static {
        System.setProperty("webdriver.chrome.driver","C:\\Users\\akcaa\\Documents\\selenium_dependencies\\drivers\\chromedriver.exe");
        //WebDriverManager.chromedriver().setup();
    }
    static ChromeOptions options = new ChromeOptions();
    static {
        options.addArguments("--disable-blink-features");
        options.addArguments("--disable-blink-features=AutomationControlled");
    }
    static WebDriver driver = new ChromeDriver(options); //new ChromeDriver(options);

   /* static {
        WebDriverManager.chromedriver().setup();
    }
    static WebDriver driver = new ChromeDriver(new ChromeOptions().addArguments("--headless"));*/


    //Actions actions = new Actions(driver);

    @Given("user goes to amazon")
    public void user_goes_to_amazon() {
       // driver = driver.Chrome(options=options, executable_path=r'C:\WebDrivers\chromedriver.exe')
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        driver.navigate().to("https://www.amazon.ca/");
        try {
            driver.findElement(By.xpath("//*[text()='Amazon']")).click();
        }catch (Exception NoSuchElementException){
            driver.findElement(By.xpath("//a[@id='nav-logo-sprites']")).click();
        }
    }

    @Then("search the upc code")
    public void search_the_upc_code() throws IOException{

        double amazonPrice = 0 ;
        double productPrice = 0;
        double amazonFee = 0;
        int starNumber = 0;
        double profit = 0;

        String path = "src/test/resources/test_data/upc.xlsx";
        FileInputStream fis = new FileInputStream(path);
        Workbook workbook = WorkbookFactory.create(fis);

        // Cell upcCell = null;
        Cell priceCell;

        String upc;

        FileWriter writer = new FileWriter("src/test/resources/test_data/goodProducts.txt",true);

        for (int i=0; i<=workbook.getSheet("Sheet1").getLastRowNum(); i++){

            try {
                WebElement searchBox = driver.findElement(By.cssSelector("#twotabsearchtextbox"));
                workbook.getSheet("Sheet1").getRow(i).getCell(0).setCellType(CellType.STRING);
                upc= workbook.getSheet("Sheet1").getRow(i).getCell(0).getStringCellValue(); // upcCell = workbook.getSheet("Sheet1").getRow(i).getCell(0);
                searchBox.sendKeys(upc.replaceAll("[^0-9]","") + Keys.ENTER); // searchBox.sendKeys(upcCell.toString().replaceAll("[^0-9]","") + Keys.ENTER);
            }catch (Exception NoSuchElementException){
                driver.navigate().back();
                WebElement searchBox1 = driver.findElement(By.cssSelector("#twotabsearchtextbox"));
                workbook.getSheet("Sheet1").getRow(i).getCell(0).setCellType(CellType.STRING);
                upc= workbook.getSheet("Sheet1").getRow(i).getCell(0).getStringCellValue();  //upcCell= workbook.getSheet("Sheet1").getRow(i).getCell(0);
                searchBox1.sendKeys(upc.replaceAll("[^0-9]","") + Keys.ENTER);
            }

            List<WebElement> liste = driver.findElements(By.xpath("//span[@class='a-price']"));
            if (!liste.isEmpty()){

                StringBuilder amazonPriceStr = new StringBuilder(liste.get(0).getText().replaceAll("[^0-9]",""));
                amazonPriceStr.replace(0,amazonPriceStr.length(), amazonPriceStr.substring(0,amazonPriceStr.length()-2) + "." + amazonPriceStr.substring(amazonPriceStr.length()-2) );

                amazonPrice = Double.parseDouble(String.valueOf(amazonPriceStr));
                amazonFee = (amazonPrice*15)/100 + 6 ;

                List<WebElement> starNumberList = driver.findElements(By.xpath("//span[@class='a-size-base s-underline-text']"));
                if (!starNumberList.isEmpty()){
                    starNumber = Integer.parseInt(starNumberList.get(0).getText().replaceAll("[^0-9]",""));
                }

            }else{
                amazonPrice = 0;
                amazonFee = 0;
                starNumber = 0;
            }

            workbook.getSheet("Sheet1").getRow(i).getCell(1).setCellType(CellType.STRING);
            priceCell= workbook.getSheet("Sheet1").getRow(i).getCell(1);
            productPrice = Double.parseDouble(priceCell.toString().replaceAll("[^0-9.]",""));

            profit = amazonPrice - (amazonFee+productPrice);

            if (profit>productPrice/5 && starNumber>30 && productPrice<=25) {
                writer.write(upc + "  ,  " + productPrice + "\n"); // writer.write(upcCell.getStringCellValue().replaceAll("[^0-9]","") + "  ,  " + productPrice + "\n");
                //System.out.println("wanted product = " + upcCell.toString() + "star number = " + starNumber + " amazon price : " + amazonPrice);
            }

            try {
                WebElement searchBox2 = driver.findElement(By.cssSelector("#twotabsearchtextbox"));
                searchBox2.clear();
            }catch (Exception NoSuchElementException){
                driver.navigate().back();
                WebElement searchBox3 = driver.findElement(By.cssSelector("#twotabsearchtextbox"));
                searchBox3.clear();
            }

            System.out.println("raw = " + (i+1) + " price " + productPrice + "  upc:" + upc);

            System.gc();
            System.runFinalization();
        }

        writer.close();

    }


}
