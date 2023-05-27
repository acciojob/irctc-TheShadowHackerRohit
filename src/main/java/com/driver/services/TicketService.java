package com.driver.services;


import com.driver.EntryDto.BookTicketEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Station;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.PassengerRepository;
import com.driver.repository.TicketRepository;
import com.driver.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    TrainRepository trainRepository;

    @Autowired
    PassengerRepository passengerRepository;

/*
Fare System : Between every consecutive stations the price is 300 Rs.
 Suppose the route is A -> B -> C -> D. incase we want to go from A to C the price will be 600,
 in case we want to go from A -> D, the totalFare will be equal to 900 and so on.

 */

    public Integer bookTicket(BookTicketEntryDto bookTicketEntryDto)throws Exception{

        //Check for validity

        //Use bookedTickets List from the TrainRepository to get bookings done against that train
        // In case the there are insufficient tickets
        // throw new Exception("Less tickets are available");

        //otherwise book the ticket, calculate the price and other details
        //Save the information in corresponding DB Tables
        //Fare System : Check problem statement

        //In case the train doesn't pass through the requested stations
        //throw new Exception("Invalid stations");

        //Save the bookedTickets in the train Object
        //Also in the passenger Entity change the attribute bookedTickets by using the attribute bookingPersonId.
       //And the end return the ticketId that has come from db


        int trainId = bookTicketEntryDto.getTrainId();
        Train train = trainRepository.findById(trainId).get();

        int noOfSeatsInTrain = train.getNoOfSeats();
        int noOfPassenger = bookTicketEntryDto.getNoOfSeats();

        if(noOfSeatsInTrain < noOfPassenger){
            throw new Exception("Less tickets are available");
        }

        String trainRoute = train.getRoute();

        String fromStation = String.valueOf(bookTicketEntryDto.getFromStation());
        String toStation = String.valueOf(bookTicketEntryDto.getToStation());

        if(trainRoute.contains(fromStation) == false && trainRoute.contains(toStation) == false){
            throw new Exception("Invalid stations");
        }


        //otherwise book the ticket, calculate the price and other details
        //Save the information in corresponding DB Tables
        //Fare System : Check problem statement

        List<Integer> passengersId = bookTicketEntryDto.getPassengerIds();
        List<Passenger> passengerList = new ArrayList<>();

        for(Integer id  : passengersId){
            Passenger passenger = passengerRepository.findById(id).get();
            passengerList.add(passenger);
        }
        //calculate fare of the journey

        String [] stations = trainRoute.split(",");
        int fromPoint = 0;
        int toPoint = 0;
        for(int i = 0 ; i < stations.length; i++){
            if(stations[i].equals(fromStation)){
                fromPoint = i;
            }
            if(stations[i].equals(toStation)){
                toPoint = i;
            }
        }
        int noOfStationInJourney = toPoint - fromPoint ;

        int fare = noOfStationInJourney * noOfPassenger * 300;


        train.setNoOfSeats(noOfSeatsInTrain-noOfPassenger);

        Ticket ticket = new Ticket();
        ticket.setFromStation(bookTicketEntryDto.getFromStation());
        ticket.setToStation(bookTicketEntryDto.getToStation());
        ticket.setTotalFare(fare);
        ticket.setPassengersList(passengerList);
        ticket.setTrain(train);

        Integer bookingPersonId = bookTicketEntryDto.getBookingPersonId();
        Passenger passenger = passengerRepository.findById(bookingPersonId).get();
        List<Ticket> bookedTicketsFromPassenger = passenger.getBookedTickets();
        bookedTicketsFromPassenger.add(ticket);

        List<Ticket> bookedTicketFromTrain = train.getBookedTickets();
        bookedTicketFromTrain.add(ticket);

        Ticket savedTicket = ticketRepository.save(ticket);

       return  savedTicket.getTicketId();

    }
}
