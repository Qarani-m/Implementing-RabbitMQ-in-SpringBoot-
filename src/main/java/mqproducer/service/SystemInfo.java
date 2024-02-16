package mqproducer.service;

import mqproducer.entity.DiskInfo;
import mqproducer.entity.OsInfo;
import mqproducer.entity.ServicesInfo;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.HashMap;
import java.util.Map;

//@Service
public class SystemInfo {
    private static final Runtime runtime = Runtime.getRuntime();
    public String osName() {
        return System.getProperty("os.name");
    }

    public  Map<String, String> osInfo() {
        int numberOfCores = runtime.availableProcessors();
        if (this.osName().contains("Linux") || this.osName().contains("Mac")){
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            numberOfCores = osBean.getAvailableProcessors();
        }
        Map<String, String> osinfo = new HashMap<>();
        osinfo.put("os", this.osName());
        osinfo.put("osVersion", System.getProperty("os.version"));
        osinfo.put("osArch", System.getProperty("os.arch"));
        osinfo.put("availableProcessors", String.valueOf(numberOfCores));
        return osinfo;
    }
    public Map<String, String> diskInfo() {
        Map<String, String> diskinfo = new HashMap<>();
        File file = new File("/");
        long usableSpace = file.getUsableSpace();
        long totalSpace = file.getTotalSpace();
        long freeSpace = file.getFreeSpace();
        String systemRoot = null;
        File[] roots = File.listRoots();
        for (File root : roots) {
            try {
                systemRoot = String.valueOf(root);
            } catch (Exception e) {
                System.err.println("Error getting disk space: " + e.getMessage());
            }
        }
        diskinfo.put("systemRoot",systemRoot );
        diskinfo.put("totalSpace",formatSize(totalSpace) );
        diskinfo.put("freeSpace",formatSize(freeSpace) );
        diskinfo.put("usableSpace", formatSize(usableSpace) );

        return diskinfo;
    }
    // Helper method to format size in a human-readable format
    private static String formatSize(long bytes) {
        final long KILOBYTE = 1024;
        final long MEGABYTE = KILOBYTE * 1024;
        final long GIGABYTE = MEGABYTE * 1024;

        if (bytes < KILOBYTE) {
            return bytes + " B";
        } else if (bytes < MEGABYTE) {
            return bytes / KILOBYTE + " KB";
        } else if (bytes < GIGABYTE) {
            return bytes / MEGABYTE + " MB";
        } else {
            return bytes / GIGABYTE + " GB";
        }
    }

    public static double getProcessCpuLoadWindows() throws Exception {
        MBeanServer mbs    = ManagementFactory.getPlatformMBeanServer();
        ObjectName name    = ObjectName.getInstance("java.lang:type=OperatingSystem");
        AttributeList list = mbs.getAttributes(name, new String[]{ "ProcessCpuLoad" });
        if (list.isEmpty())     return Double.NaN;
        Attribute att = (Attribute)list.get(0);
        Double value  = (Double)att.getValue();
        if (value == -1.0)      return Double.NaN;
        return ((int)(value * 1000) / 10.0);
    }
    public static double getProcessCpuLoadLinux() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/stat"));
            String line;
            long totalCpuTime1 = 0;
            long idleTime1 = 0;

            // Read the first line which contains total CPU time
            if ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                for (int i = 1; i < parts.length; i++) {
                    totalCpuTime1 += Long.parseLong(parts[i]);
                }
                idleTime1 = Long.parseLong(parts[4]); // Idle time
            }

            reader.close();

            // Sleep for a while
            Thread.sleep(1000);

            // Read /proc/stat again after a short delay
            reader = new BufferedReader(new FileReader("/proc/stat"));
            long totalCpuTime2 = 0;
            long idleTime2 = 0;

            // Read the first line which contains total CPU time
            if ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                for (int i = 1; i < parts.length; i++) {
                    totalCpuTime2 += Long.parseLong(parts[i]);
                }
                idleTime2 = Long.parseLong(parts[4]); // Idle time
            }

            reader.close();

            // Calculate CPU usage percentage
            long totalCpuTimeDiff = totalCpuTime2 - totalCpuTime1;
            long idleTimeDiff = idleTime2 - idleTime1;
            return 100.0 * (totalCpuTimeDiff - idleTimeDiff) / totalCpuTimeDiff;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    public static String[] getServiceNamesLinux() throws IOException {
        Process process = Runtime.getRuntime().exec("systemctl list-units --type service --state running");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder output = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        process.destroy();
        return output.toString().split("\\r?\\n");
    }
    public static String[] getServiceNamesWindows()throws IOException{return null;}
    public  double getProcessorCpuLoad() throws Exception {
        if (this.osName().contains("Linux") || this.osName().contains("Mac")){
            return  getProcessCpuLoadLinux();
        } else if (this.osName().contains("Windows")) {
            return  getProcessCpuLoadWindows();
        }else {return  0.0;}
    }
    public String[] getServiceNames() throws IOException {
        if (this.osName().contains("Linux") || this.osName().contains("Mac")){
            return  getServiceNamesLinux();
        } else if (this.osName().contains("Windows")) {
            return  getServiceNamesWindows();
        }else {return  null;}

    }


    public static void main(String[] args) throws Exception {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    SystemInfo systemInfo = new SystemInfo();
                    DiskInfo diskInfo = DiskInfo.builder()
                            .systemRoot(systemInfo.diskInfo().get("systemRoot"))
                            .totalSpace(systemInfo.diskInfo().get("totalSpace"))
                            .freeSpace( systemInfo.diskInfo().get("freeSpace"))
                            .usableSpace(systemInfo.diskInfo().get("usableSpace"))
                            .build();
                    OsInfo osInfo = OsInfo.builder()
                            .os(systemInfo.osInfo().get("os"))
                            .osVersion(systemInfo.osInfo().get("osVersion"))
                            .osArch(systemInfo.osInfo().get("osArch"))
                            .processors(systemInfo.osInfo().get("availableProcessors"))
                            .build();
                    ServicesInfo servicesInfo = ServicesInfo.builder()
                            .services(systemInfo.getServiceNames())
                            .cpuLoad(systemInfo.getProcessorCpuLoad())
                            .build();
                    System.out.println(diskInfo);
                    System.out.println(osInfo);
                    System.out.println(servicesInfo.getCpuLoad());

                    Thread.sleep(1000); // Sleep for 2 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
    }







}