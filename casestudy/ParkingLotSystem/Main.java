package ParkingLotSystem;
//Step 1 -> Create Abstract Class ParkingLotSystem
//Step 2 -> Created Method/Function -> Calculate Fee
//Step 3 -> Class Car extends ParkingLotSystem Using Inheritence
//Step 4 -> @override - Same methods with different class

// abstract class ParkingLotSystem{ // abstract -> securing data
//     String number;

//     ParkingLotSystem(String number) { //Constructor often Initialize the data of that particular class, Here it is ParkingLotSystem
//         this.number = number;
//     }

//     abstract int calculateFee(int hours);
// }
//     class Car extends ParkingLotSystem{
//         Car(String number){
//             super(number);
//         }
//         @Override
//         int calculateFee(int hours){
//             return hours * 50;

//         }   
//     }

//     class Bike extends ParkingLotSystem{
//         Bike(string number){
//             super(number);
//         }
//         @Override
//         int calculateFee(int hours){
//             return hours*20;
//         }
//     }
import java.util.ArrayList;

// Abstract class
abstract class Vehicle {
    String number;

    Vehicle(String number) {
        this.number = number;
    }

    abstract int calculateFee(int hours);
}

// Car class
class Car extends Vehicle {

    Car(String number) {
        super(number);
    }

    @Override
    int calculateFee(int hours) {
        return hours * 50;
    }
}

// Bike class
class Bike extends Vehicle {

    Bike(String number) {
        super(number);
    }

    @Override
    int calculateFee(int hours) {
        return hours * 20;
    }
}

// Parking Slot
class ParkingSlot {

    int slotNumber;
    Vehicle vehicle;

    ParkingSlot(int slotNumber) {
        this.slotNumber = slotNumber;
    }

    boolean isEmpty() {
        return vehicle == null;
    }
}

// Parking Lot
class ParkingLot {

    ArrayList<ParkingSlot> slots = new ArrayList<>();//Arraylist Object

    ParkingLot(int size) {

        for (int i = 1; i <= size; i++) {
            slots.add(new ParkingSlot(i));
        }
    }

    // Park vehicle
    void parkVehicle(Vehicle vehicle) {

        for (ParkingSlot slot : slots) {

            if (slot.isEmpty()) {

                slot.vehicle = vehicle;

                System.out.println(
                        vehicle.number +
                        " parked at slot " +
                        slot.slotNumber);

                return;
            }
        }

        System.out.println("Parking Full");
    }

    // Remove vehicle
    void removeVehicle(String number, int hours) {

        for (ParkingSlot slot : slots) {

            if (!slot.isEmpty() &&
                    slot.vehicle.number.equals(number)) {

                int fee =
                        slot.vehicle.calculateFee(hours);

                slot.vehicle = null;

                System.out.println(
                        number +
                        " removed. Fee = ₹" + fee);

                return;
            }
        }
    }

    // Display slots
    void display() {

        for (ParkingSlot slot : slots) {

            if (slot.isEmpty()) {

                System.out.println(
                        "Slot " +
                        slot.slotNumber +
                        " Empty");

            } else {

                System.out.println(
                        "Slot " +
                        slot.slotNumber +
                        " Occupied by " +
                        slot.vehicle.number);
            }
        }
    }
}

// Main class
public class Main {

    public static void main(String[] args) {

        ParkingLot p = new ParkingLot(3);

        Vehicle car = new Car("MH12AB1234");
        Vehicle bike = new Bike("MH14XY5678");

        p.parkVehicle(car);
        p.parkVehicle(bike);

        p.display();

        p.removeVehicle("MH12AB1234", 2);

        p.display();
    }
}