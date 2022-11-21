package chord.fingertable;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import chord.node.RemotePubSubChordNode;
import chord.util.Modulo;
import chord.util.Util;
import chord.util.ModuloInterval;
import pubsub.subscription.Subscription;

public class FingerTableImpl<T extends RemotePubSubChordNode<T>>
    implements FingerTable<T> {

  private final ReadWriteLock lock = new ReentrantReadWriteLock();

  private final int id;

  private final Modulo modulo;

  private final Object[] fingerTable;

  public FingerTableImpl(int degree, int id) {
    this.id = id;
    this.modulo = new Modulo(Util.powerOf2(degree));
    this.fingerTable = new Object[degree + 1];
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public int size() {
    return fingerTable.length - 1;
  }

  @Override
  public boolean containsNodeWithId(int id) {
    lock.readLock().lock();
    for (int i = 1; i < fingerTable.length; i++) {
      if (getId(i) == id) {
        lock.readLock().unlock();
        return true;
      }
    }
    lock.readLock().unlock();
    return false;
  }

  @Override
  public synchronized void set(int index, int id, T chordNode) {
    lock.writeLock().lock();
    FingerTableEntry<T> entry = new FingerTableEntry<>(id, chordNode);
    fingerTable[index] = entry;
    lock.writeLock().unlock();
  }

  @Override
  public synchronized T getNode(int index) {
    FingerTableEntry<T> fingerTableEntry = getEntry(index);
    return (T) fingerTableEntry.getNode();
  }

  @Override
  public int getId(int index) {
    FingerTableEntry<T> fingerTableEntry = getEntry(index);
    return fingerTableEntry.getId();
  }

  @SuppressWarnings("unchecked")
  private FingerTableEntry<T> getEntry(int index) {
    lock.readLock().lock();
    FingerTableEntry<T> entry = (FingerTableEntry<T>) fingerTable[index];
    lock.readLock().unlock();
    return entry;
  }

  @Override
  public ModuloInterval assignedToEntry(int i) {
    lock.readLock().lock();
    ModuloInterval assignedToEntry;
    if (i < size()) {
      int k;
      for (k = i + 1; k <= size() && getId(k) == getId(i); k++)
        ;
      if (k <= size()) {
        assignedToEntry = modulo.interval(getId(i), getId(k) - 1);
        lock.readLock().unlock();
        return assignedToEntry;
      }
    }
    assignedToEntry = modulo.interval(getId(i), this.id - 1);
    lock.readLock().unlock();
    return assignedToEntry;
  }

  @Override
  public ModuloInterval assignedToNode(int id) {
    lock.readLock().lock();
    ModuloInterval assignedToNode;
    for (int j = 1; j < fingerTable.length; j++) {
      int entryId = getId(j);
      if (entryId == id) {
        int k;
        for (k = j + 1; k <= size() && getId(k) == entryId; k++)
          ;
        if (k <= size()) {
          assignedToNode = modulo.interval(entryId, getId(k) - 1);
          lock.readLock().unlock();
          return assignedToNode;
        } else {
          assignedToNode = modulo.interval(entryId, this.id - 1);
          lock.readLock().unlock();
          return assignedToNode;
        }
      }
    }
    lock.readLock().unlock();
    return null;
  }

  @Override
  public void setSubscriptionToEntry(int index, Subscription subscription) {
    lock.writeLock().lock();
    getEntry(index).setSubscription(subscription);
    lock.writeLock().unlock();
  }

  @Override
  public void addSubscriptionToEntry(int index, Subscription subscription) {
    lock.writeLock().lock();
    getEntry(index).addSubscription(subscription);
    lock.writeLock().unlock();
  }

  @Override
  public void addSubscriptionToNode(int id, Subscription subscription) {
    lock.writeLock().lock();
    for (int i = 1; i < fingerTable.length; i++) {
      if (getId(i) == id) {
        addSubscriptionToEntry(i, subscription);
      }
    }
    lock.writeLock().unlock();
  }

  @Override
  public Subscription getSubscriptionOfEntry(int index) {
    FingerTableEntry<T> entry = getEntry(index);
    return entry.getSubscription();

  }

  @Override
  public void removeSubscriptionOfEntry(int index, Subscription subscription) {
    lock.writeLock().lock();
    getEntry(index).removeSubscription(subscription);
    lock.writeLock().unlock();
  }

  @Override
  public String toString() {
    lock.readLock().lock();
    StringBuilder s = new StringBuilder();
    s.append('[');
    for (int i = 0; i < fingerTable.length; i++) {
      if (fingerTable[i] == null) {
        s.append("null");
      } else {
        s.append(
            String.format("\nEntry %d: successor of %d: %d", i,
                (id + Util.powerOf2(i - 1)) % Util.powerOf2((fingerTable.length - 1)),
                getId(i)));
      }
      if (i == fingerTable.length - 1) {
      } else {
        s.append(", ");
      }
    }
    s.append("\n]");
    lock.readLock().unlock();
    return s.toString();
  }
}
