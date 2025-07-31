package in.apcfss.model;

public class ApplicationVariables {
public static final String contextName = "apolcms";
public static final String filesepartor = System.getProperty("file.separator");
public static final String contextPath;
public static final String filesPath;
public static final String ackPath;
public static final String InstructionPath;
static final String apolcmsDataBase = "jdbc:postgresql://10.96.54.54:6432/apolcms";
static final String apolcmsUserName = "apolcms";
static final String apolcmsPassword = "@p0l(m$";

static {
  contextPath = System.getProperty("catalina.base") + filesepartor + "webapps" + filesepartor + "apolcms" + filesepartor;
  filesPath = contextPath + "files" + filesepartor;
  ackPath = "uploads" + filesepartor + "acks" + filesepartor;
   InstructionPath = "uploads"+filesepartor+"Instruction"+filesepartor;
}
}