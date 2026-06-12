package edu.eci.arsw.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class LabInventoryServiceImpl extends UnicastRemoteObject implements LabInventoryService {
    private Map<String, Equipment> inventory = new HashMap<>();

    public LabInventoryServiceImpl() throws RemoteException {
        inventory.put("PC-01", new Equipment("PC-01", "Dell Optiplex", "Lab A"));
        inventory.put("PC-02", new Equipment("PC-02", "HP EliteDesk", "Lab A"));
        inventory.put("ARD-01", new Equipment("ARD-01", "Arduino Uno", "Lab B"));
        inventory.put("ARD-02", new Equipment("ARD-02", "Arduino Mega", "Lab B"));
        inventory.put("OSC-01", new Equipment("OSC-01", "Osciloscopio Rigol", "Lab C"));
    }

    @Override
    public List<String> consultarEquipos() throws RemoteException {
        List<String> result = new ArrayList<>();
        for (Equipment eq : inventory.values()) result.add(eq.toString());
        return result;
    }

    @Override
    public String consultarEquipo(String codigo) throws RemoteException {
        Equipment eq = inventory.get(codigo);
        return eq == null ? "ERROR: equipment not found" : eq.toString();
    }

    @Override
    public boolean reservarEquipo(String codigo) throws RemoteException {
        Equipment eq = inventory.get(codigo);
        if (eq == null || eq.isReserved()) return false;
        eq.reserve(); return true;
    }

    @Override
    public boolean liberarEquipo(String codigo) throws RemoteException {
        Equipment eq = inventory.get(codigo);
        if (eq == null || !eq.isReserved()) return false;
        eq.release(); return true;
    }
}