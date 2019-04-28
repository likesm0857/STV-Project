import adapter.CommandMapper;
import adapter.ConfigReader;
import adapter.device.AppiumDriver;
import adapter.device.DeviceDriver;
import adapter.parser.TestDataParser;
import adapter.scriptGenerator.ICommandMapper;
import adapter.scriptGenerator.ScriptGenerator;
import entity.TestData;
import useCase.command.CommandFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ScriptMain {
    static final int EXECUTE_INSTRUCTION = 1;
    static final int EXIT_PROGRAM = 2;

    public static void main(String[] args) throws Exception {
        ConfigReader configReader = new ConfigReader();
        DeviceDriver driver = new AppiumDriver(configReader.getConfig());
        driver.startService();
        TestDataParser testDataParser = new TestDataParser("./TestData/TestData.xlsx");
        testDataParser.parse();
        TestData testData = testDataParser.getTestData();

        CommandFactory commandFactory = new CommandFactory(driver);
        ICommandMapper commandMapper = new CommandMapper(testData, commandFactory);



        ScriptGenerator scriptGenerator = new ScriptGenerator(commandMapper);

        while (true) {
            displayFunctionality();

            int choice = enterChoice();

            if (choice == EXIT_PROGRAM)
                break;

            if (choice == EXECUTE_INSTRUCTION) {
                handleExecuteInstruction(scriptGenerator);
            } else {
                System.out.println("This Choice has no functionality, input again!");
            }
        }
        handleInstructionSaving(scriptGenerator);
    }

    public static void handleInstructionSaving(ScriptGenerator scriptGenerator) {
        scriptGenerator.writeScriptFile();
    }

    public static void displayFunctionality() {
        System.out.println("1. enter script");
        System.out.println("2. stop enter");
        System.out.print("enter choice: ");
    }

    static int enterChoice() {
        int choice;
        while (true) {
            Scanner in = new Scanner(System.in);

            try {
                choice = in.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.print("Invalid input, enter again!: ");
            }
        }
        return choice;
    }

    private static void handleExecuteInstruction(ScriptGenerator scriptGenerator) {
        System.out.print("enter script: ");
        String instruction = enterInstruction();

        System.out.println(instruction);

        try {
            scriptGenerator.executeInstruction(instruction);
        } catch (Exception e) {

        }

        storeInstruction(scriptGenerator);
    }

    private static String enterInstruction() {

        BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
        String instruction = "";
        try {
            instruction = buf.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return instruction;
    }

    static void storeInstruction(ScriptGenerator scriptGenerator) {
        while (true) {
            String storeChoice = enterStoreChoice();
            if (storeChoice.equals("Y")) {
                break;
            }
            else if (storeChoice.equals("N")) { // checking content of string, not reference.
                System.out.println("Remove Success");
                scriptGenerator.removeCurrentInstruction();
                break;
            } else {
                System.out.print("Invalid input, enter again!: ");
            }
        }
    }

    private static String enterStoreChoice() {
        System.out.print("Did you want to store instruction? (Y/N): ");
        Scanner s = new Scanner(System.in);
        String storeChoice = s.next();
        return storeChoice;
    }
}
