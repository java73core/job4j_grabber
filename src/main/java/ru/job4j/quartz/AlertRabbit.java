package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import java.io.InputStream;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit implements AutoCloseable {

    private Connection initConnection(Properties properties) throws Exception {
        Class.forName(properties.getProperty("jdbc.driver"));
        String url = properties.getProperty("jdbc.url");
        String login = properties.getProperty("jdbc.username");
        String password = properties.getProperty("jdbc.password");
        return DriverManager.getConnection(url, login, password);
    }

    public static int getTime() {
        int value = 0;
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            Properties properties = new Properties();
            properties.load(in);
            value = Integer.parseInt(properties.getProperty("rabbit.interval"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return value;
    }

    public Properties getProperties() {
        Properties properties = new Properties();
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            properties.load(in);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return properties;
    }

    public void createTable(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            String sql = String.format(
                    "create table if not exists rabbit(%s, %s);",
                    "id serial primary key",
                    "created_date timestamp"
            );
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        AlertRabbit alertRabbit = new AlertRabbit();
        try (Connection connection = alertRabbit.initConnection(alertRabbit.getProperties())) {
            alertRabbit.createTable(connection);
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connection", connection);
            JobDetail job = newJob(Rabbit.class)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(5)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(getTime());
            scheduler.shutdown();
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {

    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            try (Connection connection = (Connection) context.getJobDetail().getJobDataMap().get("connection")) {
                PreparedStatement statement = connection.prepareStatement("insert into rabbit(created_date) values (?)");
                DateFormat timeStamp = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                statement.setTimestamp(1, Timestamp.valueOf(timeStamp.format(System.currentTimeMillis())));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("Rabbit runs here ...");
        }
    }
}