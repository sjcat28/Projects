import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import  javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class WeatherAppGUI extends JFrame{
    private JSONObject weatherData;

    public WeatherAppGUI(){

        super("Weather app");


        //configuring it to end the programonce it's closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //setting size of GUI
        setSize(450,650);

        //setting the gui in the centre of the screen
        setLocationRelativeTo(null);

        //setting the layout manager to be null for manually positionning components
        setLayout(null);

        //preventing any resize of our gui
        setResizable(false);

        addGuiComponents();
    }

    /**
     * addes the components for the GUI to the frame
     */
    private void addGuiComponents(){
        //adding search field and setting its location and size
        JTextField searchField = new JTextField();
        searchField.setBounds(15, 15, 351, 45);
        searchField.setFont(new Font("Dialog", Font.PLAIN, 24));
        add(searchField);

        //creating the Search button, by making a button and loading the asset
        //JButton searchButton = new JButton(loadImage("src/assets/search.png"));
        //searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        //searchButton.setBounds(375, 13, 47, 45);

        //add(searchButton);

        //Adding the weather icon
        JLabel weatherIcon = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherIcon.setBounds(0,125,450,217);
        add(weatherIcon);

        //adding the temperature icon
        JLabel temperatureText = new JLabel("");
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));
        temperatureText.setBounds(0,350,450,54);

        //centering the text
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        //weather description
        JLabel weatherDesc = new JLabel("Cloudy");
        weatherDesc.setBounds(0,405, 450, 36);
        weatherDesc.setFont(new Font("Dialog", Font.BOLD, 32));
        weatherDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherDesc);

        //adding the humidity icon
        JLabel humidityIcon = new JLabel(loadImage("src/assets/humidity.png"));
        humidityIcon.setBounds(0,550,74,66);
        add(humidityIcon);

        //adding humidity text
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(75,550,80,66);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        //adding wind speed Icon
        JLabel windIcon = new JLabel(loadImage("src/assets/windspeed.png"));
        windIcon.setBounds(240, 550,74,66);
        add(windIcon);

        //adding wind speed text
        JLabel windText = new JLabel("<html><b>WindSpeed</b> 100km/h</html>");
        windText.setBounds(320, 550, 90, 66);
        windText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windText);

        //creating the Search button, by making a button and loading the asset
        JButton searchButton = new JButton(loadImage("src/assets/search.png"));
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);


        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //getting location from user
                String userInput = searchField.getText();

                //validating the input, taking out white spaces ensuring no empty inputs
                if(userInput.replaceAll("\\s", "").length() <= 0){
                    //do nothing
                    return;
                }

                //getting weather data
                weatherData = WeatherAppData.getWeatherData(userInput);

                //getting all the data needed from the weatherData and updating GUI
                String weatherCondition = (String) weatherData.get("weather_condition");

                double temperature = (double) weatherData.get("temperature");

                long humidity = (long) weatherData.get("humidity");

                double windSpeed = (double) weatherData.get("wind_speed");



                //changing the weather icon and description
                switch (weatherCondition){
                    case "Clear Sky":
                        weatherIcon.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherIcon.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    case "Rainy":
                        weatherIcon.setIcon(loadImage("src/assets/rain.png"));
                        break;
                    case "Snowy":
                        weatherIcon.setIcon(loadImage("src/assets/snow.png"));
                        break;
                }

                weatherDesc.setText(weatherCondition);

                //changing the temperature text
                temperatureText.setText(temperature + "C");

                //changing humidity text
                humidityText.setText("<html><b>Humidity\n</b>" + humidity + "%</html>");

                //changing the wind speed text
                windText.setText("<html><b>Wind speed\n</b>" + windSpeed + "Km/h</html>");
            }
        });

        add(searchButton);
    }

    /**
     * method for loading assets onto the GUI
     * @param path
     * @return
     */
    private ImageIcon loadImage(String path){
        try{
            BufferedImage image = ImageIO.read(new File(path));

            return new ImageIcon(image);
        } catch(Exception e){ e.getStackTrace();}
        return null;
    }
}
