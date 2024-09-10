import javax.swing.*;

public class Launcher {
    public static void main(String args[]){
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run(){
                //setting the weather app to be visible
                new WeatherAppGUI().setVisible(true);
                //System.out.println(WeatherAppData.getLocationData("Wellington"));
                //System.out.println(WeatherAppData.getCurrentTime());
            }

        });
    }
}
