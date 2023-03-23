package gatling.utils;

import gatling.envconfig.manager.RunningConfiguration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestingSpecs {

    private static RunningConfiguration runningConfiguration = new RunningConfiguration();
    private static String apiUser = "Rest AUTOMATION";

    @SneakyThrows
    public static String getTestingEnvironment(){
        return runningConfiguration.getEnvironmentUrlForMarket(getEnvironment()) + "/sormas-rest/";
    }

    @SneakyThrows
    public static int getNumberOfUsers(){
        try{
           return Integer.parseInt(System.getProperty("users"));
        }
        catch (Exception any){
            throw new Exception("Number of users wasn't provided!");
        }
    }

    @SneakyThrows
    public static int getExecutionTime(){
        try{
            return Integer.parseInt(System.getProperty("time"));
        }
        catch (Exception any){
            throw new Exception("Maximum execution time wasn't provided!");
        }
    }

    public static String getUsername(){
        return runningConfiguration.getUserByRole(getEnvironment(), apiUser).getUsername();
    }

    public static String getPassword(){
        return runningConfiguration.getUserByRole(getEnvironment(), apiUser).getPassword();
    }

    @SneakyThrows
    private static String getEnvironment(){
        try{
           return System.getProperty("env");
        }
        catch (Exception any){
            throw new Exception("Testing environment wasn't provided!");
        }
    }
}
