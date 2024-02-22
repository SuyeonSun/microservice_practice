package com.programmingtechie.order.service.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.programmingtechie.order.service.dto.InventoryResponse;
import com.programmingtechie.order.service.dto.OrderLineItemsDto;
import com.programmingtechie.order.service.dto.OrderRequest;
import com.programmingtechie.order.service.model.Order;
import com.programmingtechie.order.service.model.OrderLineItems;
import com.programmingtechie.order.service.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
	private final OrderRepository orderRepository;
	private final WebClient webClient;

	public void placeOrder(OrderRequest orderRequest) {
		Order order = new Order();
		order.setOrderNumber(UUID.randomUUID().toString());

		List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
			.stream()
			.map(this::mapToDto)
			.toList();

		order.setOrderLineItemsList(orderLineItems);

		List<String> skuCodes = order.getOrderLineItemsList().stream()
			.map(OrderLineItems::getSkuCode)
			.toList();

		// call Inventory Service, and place order if product is in
		// stock
		InventoryResponse[] inventoryResponseArray = webClient.get()
			.uri("http://localhost:8082/api/inventory",
				uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
			.retrieve()
			.bodyToMono(InventoryResponse[].class) // read data from webClient
			.block(); // to make a synchronous request

		// System.out.println(inventoryResponseArray.length); // 0

		boolean allProductsInStock = Arrays.stream(inventoryResponseArray)
			.allMatch(inventoryResponse -> inventoryResponse.getIsInStock());
			// .allMatch(InventoryResponse::getIsInStock);

		if (allProductsInStock) {
			orderRepository.save(order);
		} else {
			throw new IllegalArgumentException("Product is not in stock, please try later.");
		}
	}

	private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
		OrderLineItems orderLineItems = new OrderLineItems();
		orderLineItems.setPrice(orderLineItemsDto.getPrice());
		orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
		orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
		return orderLineItems;
	}
}
