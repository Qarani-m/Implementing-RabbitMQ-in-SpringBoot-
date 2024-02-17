package mqproducer.service;

import mqproducer.entity.DiskInfo;
import org.springframework.stereotype.Service;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

@Service
public class SystemInfo {

    private static Runtime runtime = Runtime.getRuntime();

    public String info() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.osInfo());
        sb.append(this.memInfo());
        sb.append(this.diskInfo());
        return sb.toString();
    }

    public String osName() {
        return System.getProperty("os.name");
    }

    public String osVersion() {
        return System.getProperty("os.version");
    }

    public String osArch() {
        return System.getProperty("os.arch");
    }

    public long totalMem() {
        return Runtime.getRuntime().totalMemory();
    }

    public long usedMem() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    public  String memInfo() {
        NumberFormat format = NumberFormat.getInstance();
        StringBuilder sb = new StringBuilder();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        sb.append("Free memory: ");
        sb.append(format.format(freeMemory / 1024));
        sb.append("<br/>");
        sb.append("Allocated memory: ");
        sb.append(format.format(allocatedMemory / 1024));
        sb.append("<br/>");
        sb.append("Max memory: ");
        sb.append(format.format(maxMemory / 1024));
        sb.append("<br/>");
        sb.append("Total free memory: ");
        sb.append(format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024));
        sb.append("<br/>");
        return sb.toString();

    }

    public String osInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("OS: ");
        sb.append(this.osName());
        sb.append("<br/>");
        sb.append("Version: ");
        sb.append(this.osVersion());
        sb.append("<br/>");
        sb.append(": ");
        sb.append(this.osArch());
        sb.append("<br/>");
        sb.append("Available processors (cores): ");
        sb.append(runtime.availableProcessors());
        sb.append("<br/>");
        return sb.toString();
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

    public double getProcessCpuLoad() throws Exception {
        MBeanServer mbs    = ManagementFactory.getPlatformMBeanServer();
        ObjectName name    = ObjectName.getInstance("java.lang:type=OperatingSystem");
        AttributeList list = mbs.getAttributes(name, new String[]{ "ProcessCpuLoad" });
        if (list.isEmpty())     return Double.NaN;
        Attribute att = (Attribute)list.get(0);
        Double value  = (Double)att.getValue();
        if (value == -1.0)      return Double.NaN;
        return ((int)(value * 1000) / 10.0);
    }

    public static void main(String[] args) throws Exception {
        SystemInfo systemInfo = new SystemInfo();

        DiskInfo diskInfo =new DiskInfo(
                systemInfo.diskInfo().get("systemRoot"),
                systemInfo.diskInfo().get("totalSpace"),
                systemInfo.diskInfo().get("freeSpace"),
                systemInfo.diskInfo().get("usableSpace")
        );






//        System.out.println(systemInfo.getProcessCpuLoad());
//        System.out.println(systemInfo.osInfo());



        systemInfo.diskInfo();



    }
}