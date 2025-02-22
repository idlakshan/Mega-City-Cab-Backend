package com.mcc.backend.bo.custom.impl;

import com.mcc.backend.bo.custom.DriverBO;
import com.mcc.backend.dao.custom.DriverDAO;
import com.mcc.backend.dao.custom.impl.DriverDAOImpl;
import com.mcc.backend.dto.DriverDTO;
import com.mcc.backend.entity.Driver;
import com.mcc.backend.servlet.BookingServlet;
import com.mcc.backend.servlet.DriverServlet;
import com.mcc.backend.servlet.StripeCheckoutServlet;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DriverBOImpl implements DriverBO {

    private final DriverDAO driverDAO = new DriverDAOImpl();

    @Override
    public boolean saveDriver(DriverDTO driverDTO) throws SQLException, ClassNotFoundException {
        try (Connection conn = DriverServlet.dataSource.getConnection()) {
            Driver driver = new Driver();
            driver.setDriverName(driverDTO.getDriverName());
            driver.setDriverNic(driverDTO.getDriverNic());
            driver.setDriverAddress(driverDTO.getDriverAddress());
            driver.setDriverEmail(driverDTO.getDriverEmail());
            driver.setLicenseImage(driverDTO.getLicenseImage());
            driver.setDriverContact(driverDTO.getDriverContact());
            return driverDAO.save(conn, driver);
        }
    }

    @Override
    public List<DriverDTO> getAllDrivers() throws SQLException, ClassNotFoundException {
        try (Connection conn = DriverServlet.dataSource.getConnection()) {
            List<Driver> drivers = driverDAO.findAll(conn);
            List<DriverDTO> driverDTOs = new ArrayList<>();
            for (Driver driver : drivers) {
                DriverDTO driverDTO = new DriverDTO(
                        driver.getDriverId(),
                        driver.getDriverName(),
                        driver.getDriverNic(),
                        driver.getDriverAddress(),
                        driver.getDriverEmail(),
                        driver.getLicenseImage(),
                        driver.getDriverContact(),
                        driver.getStatus()
                );
                driverDTOs.add(driverDTO);
            }
            return driverDTOs;
        }
    }

    @Override
    public DriverDTO getDriverById(int driverId) throws SQLException, ClassNotFoundException {
        try (Connection conn = DriverServlet.dataSource.getConnection()) {
            Driver driver = driverDAO.findById(conn, driverId);
            if (driver != null) {
                return new DriverDTO(
                        driver.getDriverId(),
                        driver.getDriverName(),
                        driver.getDriverNic(),
                        driver.getDriverAddress(),
                        driver.getDriverEmail(),
                        driver.getLicenseImage(),
                        driver.getDriverContact(),
                        driver.getStatus()
                );
            }
            return null;
        }
    }

    @Override
    public boolean updateDriver(DriverDTO driverDTO) throws SQLException, ClassNotFoundException {
        try (Connection conn = DriverServlet.dataSource.getConnection()) {
            Driver driver = new Driver();
            driver.setDriverId(driverDTO.getDriverId());
            driver.setDriverName(driverDTO.getDriverName());
            driver.setDriverNic(driverDTO.getDriverNic());
            driver.setDriverAddress(driverDTO.getDriverAddress());
            driver.setDriverEmail(driverDTO.getDriverEmail());
            driver.setLicenseImage(driverDTO.getLicenseImage());
            driver.setDriverContact(driverDTO.getDriverContact());
            driver.setStatus(driverDTO.getStatus());
            return driverDAO.update(conn, driver);
        }
    }

    @Override
    public boolean deleteDriver(int driverId) throws SQLException, ClassNotFoundException {
        try (Connection conn = DriverServlet.dataSource.getConnection()) {
            return driverDAO.delete(conn, driverId);
        }
    }

    @Override
    public List<DriverDTO> getAvailableDrivers() throws Exception {
        try (Connection conn = BookingServlet.dataSource.getConnection()) {
            List<Driver> drivers = driverDAO.getAvailableDrivers(conn);
            List<DriverDTO> driverDTOs = new ArrayList<>();
            for (Driver driver : drivers) {
                DriverDTO driverDTO = new DriverDTO(
                        driver.getDriverId(),
                        driver.getDriverName(),
                        driver.getDriverNic(),
                        driver.getDriverAddress(),
                        driver.getDriverEmail(),
                        driver.getLicenseImage(),
                        driver.getDriverContact(),
                        driver.getStatus()
                );
                driverDTOs.add(driverDTO);
            }
            return driverDTOs;
        }
    }

    @Override
    public int getActiveDrivers() throws SQLException, ClassNotFoundException {
        try (Connection conn = BookingServlet.dataSource.getConnection()) {
            return driverDAO.getActiveDrivers(conn);
        }
    }

    @Override
    public void updateDriverStatus(DriverDTO driverDTO) throws Exception {
        try (Connection conn = StripeCheckoutServlet.dataSource.getConnection()) {
            Driver driver = new Driver();
            driver.setDriverId(driverDTO.getDriverId());
            driver.setStatus(driverDTO.getStatus());
            driverDAO.updateDriverStatus(conn,driver);
        }
    }
}