/*
 * Copyright (c) 2025 Oracle and/or its affiliates.
 * Copyright (c) 2025 IBM Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.test.model;

import java.time.Year;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "HOUSE")
@NamedQuery(name="House.all", query="SELECT h FROM House h")
public class House {

    private static final String ID_GENERATOR = "HOUSE_ID_GENERATOR";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_GENERATOR)
    @SequenceGenerator(name = ID_GENERATOR, sequenceName = ID_GENERATOR, allocationSize = 1)
    private int id;

    private int area;

    private Garage garage;

    private Kitchen kitchen;

    private float lotSize;

    private int numBedrooms;

    private String parcelId;

    private float purchasePrice;

    private Year sold;

    public House(int id,
                 int area,
                 Garage garage,
                 Kitchen kitchen,
                 float lotSize,
                 int numBedrooms,
                 String parcelId,
                 float purchasePrice,
                 Year sold) {
        this.id = id;
        this.area = area;
        this.garage = garage;
        this.kitchen = kitchen;
        this.lotSize = lotSize;
        this.numBedrooms = numBedrooms;
        this.parcelId = parcelId;
        this.purchasePrice = purchasePrice;
        this.sold = sold;
    }

    public House() {
        this(-1, -1, null, null, -1f, -1, null, -1, null);
    }

    public House(int area,
                 Garage garage,
                 Kitchen kitchen,
                 float lotSize,
                 int numBedrooms,
                 String parcelId,
                 float purchasePrice,
                 Year sold) {
        this(-1, area, garage, kitchen, lotSize, numBedrooms, parcelId, purchasePrice, sold);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public Garage getGarage() {
        return garage;
    }

    public void setGarage(Garage garage) {
        this.garage = garage;
    }

    public Kitchen getKitchen() {
        return kitchen;
    }

    public void setKitchen(Kitchen kitchen) {
        this.kitchen = kitchen;
    }

    public float getLotSize() {
        return lotSize;
    }

    public void setLotSize(float lotSize) {
        this.lotSize = lotSize;
    }

    public int getNumBedrooms() {
        return numBedrooms;
    }

    public void setNumBedrooms(int numBedrooms) {
        this.numBedrooms = numBedrooms;
    }

    public String getParcelId() {
        return parcelId;
    }

    public void setParcelId(String parcelId) {
        this.parcelId = parcelId;
    }

    public float getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(float purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Year getSold() {
        return sold;
    }

    public void setSold(Year sold) {
        this.sold = sold;
    }

}
