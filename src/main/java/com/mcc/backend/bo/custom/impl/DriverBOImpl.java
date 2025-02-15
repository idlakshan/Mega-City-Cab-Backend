package com.mcc.backend.bo.custom.impl;

import com.mcc.backend.bo.custom.DriverBO;
import com.mcc.backend.dto.DriverDTO;
import com.mcc.backend.dao.custom.DriverDAO;
import com.mcc.backend.dao.custom.impl.DriverDAOImpl;
import com.mcc.backend.entity.Driver;
import com.mcc.backend.servlet.DriverServlet;

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
            driver.setDriverEmail(driverDTO.getDriverEmail());
            driver.setLicenseImage(driverDTO.getLicenseImage());
            driver.setDriverContact(driverDTO.getDriverContact());
            return driverDAO.saveDriver(conn, driver);
        }
    }

    @Override
    public List<DriverDTO> getAllDrivers() throws SQLException, ClassNotFoundException {
        try (Connection conn = DriverServlet.dataSource.getConnection()) {
            List<Driver> drivers = driverDAO.getAllDrivers(conn);
            List<DriverDTO> driverDTOs = new ArrayList<>();
            for (Driver driver : drivers) {
                driverDTOs.add(new DriverDTO(
                        driver.getDriverId(),
                        driver.getDriverName(),
                        driver.getDriverNic(),
                        driver.getDriverAddress(),
                        driver.getDriverEmail(),
                        driver.getLicenseImage(),
                        driver.getDriverContact(),
                        driver.getStatus()
                ));
            }
            return driverDTOs;
        }
    }

    @Override
    public DriverDTO getDriverById(int driverId) throws SQLException, ClassNotFoundException {
        try (Connection conn = DriverServlet.dataSource.getConnection()) {
            Driver driver = driverDAO.getDriverById(conn, driverId);
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
    public boolean updateDriver(DriverDTO dto) throws SQLException, ClassNotFoundException {
        try (Connection conn = DriverServlet.dataSource.getConnection()) {
            Driver driver = new Driver();

            driver.setDriverId(dto.getDriverId());
            driver.setDriverName(dto.getDriverName());
            driver.setDriverNic(dto.getDriverNic());
            driver.setDriverAddress(dto.getDriverAddress());
            driver.setDriverEmail(dto.getDriverEmail());
            driver.setLicenseImage(dto.getLicenseImage());
            driver.setDriverContact(dto.getDriverContact());
            driver.setStatus(dto.getStatus());
            return driverDAO.updateDriver(conn, driver);
        }
    }

    @Override
    public boolean deleteDriver(int driverId) throws SQLException, ClassNotFoundException {
        try (Connection conn = DriverServlet.dataSource.getConnection()) {
            return driverDAO.deleteDriver(conn, driverId);
        }
    }
}