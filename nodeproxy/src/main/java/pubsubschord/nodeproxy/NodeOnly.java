package pubsubschord.nodeproxy;

import java.lang.System.Logger;
import java.rmi.RemoteException;

public class NodeOnly {

  private static final Logger log = System.getLogger(NodeOnly.class.getName());

  public static void main(String[] args) throws RemoteException {
    try {
      Node node = Node.getInstance();
    } catch (RemoteException e) {
      throw e;
    }
  }
}
