package com.github.mattrandom.inventoryservice;

import com.github.mattrandom.inventory_service.InventoryServiceApplication;
import org.springframework.boot.SpringApplication;

public class TestInventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(InventoryServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
