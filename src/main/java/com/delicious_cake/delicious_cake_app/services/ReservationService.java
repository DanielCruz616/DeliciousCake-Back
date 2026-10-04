package com.delicious_cake.delicious_cake_app.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.delicious_cake.delicious_cake_app.dtos.ReservationDTO;
import com.delicious_cake.delicious_cake_app.entities.CustomerEntity;
import com.delicious_cake.delicious_cake_app.entities.ReservationEntity;
import com.delicious_cake.delicious_cake_app.mappers.ReservationMapper;
import com.delicious_cake.delicious_cake_app.repositories.CustomerRepository;
import com.delicious_cake.delicious_cake_app.repositories.ReservationRepository;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CustomerRepository customerRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            CustomerRepository customerRepository) {

        this.reservationRepository = reservationRepository;
        this.customerRepository = customerRepository;
    }

    //Create Method
    public ReservationDTO createReservation(ReservationDTO dto) {

        validateReservation(dto);

        ReservationEntity reservation =
                ReservationMapper.toEntity(dto);

        CustomerEntity customer =
                customerRepository.findById(dto.getCustomerId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Customer not found with id: "
                                                + dto.getCustomerId()));

        reservation.setCustomer(customer);
        reservation.setCreatedAt(LocalDate.now());

        ReservationEntity savedReservation =
                reservationRepository.save(reservation);

        return ReservationMapper.toDTO(savedReservation);
    }

    //Get Method
    public ReservationDTO getReservationById(Long id) {

        ReservationEntity reservation =
                reservationRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Reservation not found with id: " + id));

        return ReservationMapper.toDTO(reservation);
    }

    //Get All Method
    public List<ReservationDTO> getAllReservations() {

        return reservationRepository.findAll()
                .stream()
                .map(ReservationMapper::toDTO)
                .collect(Collectors.toList());
    }

    //Update Method
    public ReservationDTO updateReservation(
            Long id,
            ReservationDTO dto) {

        ReservationEntity existingReservation =
                reservationRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Reservation not found with id: " + id));

        validateReservation(dto);

        CustomerEntity customer =
                customerRepository.findById(dto.getCustomerId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Customer not found with id: "
                                                + dto.getCustomerId()));

        existingReservation.setCustomer(customer);
        existingReservation.setDescription(dto.getDescription());
        existingReservation.setPickupAt(dto.getPickupAt());
        existingReservation.setTotal(dto.getTotal());
        existingReservation.setPending(dto.getPending());

        ReservationEntity updatedReservation =
                reservationRepository.save(existingReservation);

        return ReservationMapper.toDTO(updatedReservation);
    }

    //Delete Method
    public void deleteReservation(Long id) {

        ReservationEntity reservation =
                reservationRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Reservation not found with id: " + id));

        reservationRepository.delete(reservation);
    }

    //Validation Method
    private void validateReservation(ReservationDTO dto) {

        if (dto.getCustomerId() == null) {
            throw new IllegalArgumentException(
                    "Customer ID cannot be null");
        }

        if (dto.getPickupAt() == null) {
            throw new IllegalArgumentException(
                    "PickupAt cannot be null");
        }

        if (dto.getTotal() == null ||
                dto.getTotal().compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Total cannot be null or negative");
        }

        if (dto.getPending() == null ||
                dto.getPending().compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Pending cannot be null or negative");
        }
    }
}