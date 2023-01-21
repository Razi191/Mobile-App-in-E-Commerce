# import json
# from rest_framework.decorators import api_view
# from rest_framework.response import Response
# from django.shortcuts import render
# from .serializers import CartSerializer
# from .models.models import Cart
# from .models.models import Inventory
# from .models.models import Stores
# from .models.models import Reports
# import numpy as np
# from .models.models import Warnings
# from .models.models import Sanctions
# import operator
#
#
# def getWatad(request):
#     return render(request, 'watad.html')
#
# @api_view(['GET'])
# def getRoutes(request):
#     routes = [
#         {
#             'Endpoint': '/cart/',
#             'method': 'GET',
#             'body': None,
#             'description': 'Returns an array of Items'
#         },
#         {
#             'Endpoint': '/cart/id',
#             'method': 'GET',
#             'body': None,
#             'description': 'Returns a single Item'
#         },
#         {
#             'Endpoint': '/cart/add',
#             'method': 'POST',
#             'body': None,
#             'description': 'Add new Item to a cart'
#         },
#         {
#             'Endpoint': '/cart/id/update',
#             'method': 'PUT',
#             'body': None,
#             'description': 'Creates an existing cart with data sent in'
#         },
#         {
#             'Endpoint': '/cart/id/delete',
#             'method': 'DELETE',
#             'body': None,
#             'description': 'Deletes an existing Item'
#         }
#     ]
#     return Response(routes)
#
#
# @api_view(['GET'])
# def getCart(request):
#     Carts = Cart.objects.all()
#     serializer = CartSerializer(Carts, many=True)
#     return Response(serializer.data)
#
#
# @api_view(['GET'])
# def getItem(request, pk):
#     Item = Cart.objects.get(id=pk)
#     serializer = CartSerializer(Item, many=False)
#     return Response(serializer.data)
#
#
# @api_view(['GET'])
# def getPrice(request, store_id, barCode):
#     if store_id in Inventory.inventory:
#         if barCode in Inventory.inventory[store_id]:
#             store_price = Inventory.inventory[store_id][barCode][1]
#             predicted = predict(store_id, barCode)
#             if predicted is None:
#                 return Response("Price =" + str(store_price))
#             else:
#                 return Response("Price =" + str(store_price) + "  Predicted price according to reports =" + str(predicted))
#         else:
#             return Response('Item does not exist.')
#     else:
#         return Response('store does not exist.')
#
#
# @api_view(['GET'])
# def getStores(request):
#     return Response(Stores.stores)
#
#
# @api_view(['POST'])
# def addItem(request, store_id, barCode, count):
#     if barCode in Inventory.inventory:
#         if store_id in Stores.stores.keys():
#             name = Inventory.inventory[store_id][barCode][1]
#             Item = Cart.objects.create(
#                 body = name,
#                 barCode = barCode,
#                 count = count
#             )
#         else:
#             return Response('store does not exist.')
#     else:
#         return Response('Item not supported.')
#     serilizers = CartSerializer(Item, many=False)
#     return Response(serilizers.data)
#
#
# @api_view(['PUT'])
# def updateItem(request, pk):
#     data = request.data
#
#     Item = Cart.objects.get(id=pk)
#
#     serializer = CartSerializer(Item, data=request.data)
#     if serializer.is_valid():
#         serializer.save()
#
#     return Response(serializer.data)
#
#
# @api_view(['DELETE'])
# def deleteItem(request, pk):
#     Item = Cart.objects.get(id=pk)
#     Item.delete()
#     return Response('Item DELETED.')
#
#
#
#
# @api_view(['GET'])
# def reportPrice(request, store_id, barCode, price):
#     if store_id in Inventory.inventory:
#         if barCode in Inventory.inventory[store_id]:
#
#             ##if (price == Inventory.inventory[store_id][barCode][1]):
#             ##      return Response('The product is compatible!')
#
#             Reports.reports[store_id][barCode].append(price) ## we could add a date arg also
#             validate(store_id, barCode)
#             return Response('Report has been sent.')
#         else:
#             Response('Item not supported.')
#     else:
#         return Response('store does not exist.')
#
#
#
# def validate(store_id, barCode):
#     arr = Reports.reports[store_id][barCode].sort()
#     if arr is None or len(arr) == 0:
#         return None
#     b = len(arr)
#     arr = np.array(arr)
#     a = int(0.05*b)
#     arr = arr[a:b-a]
#     if len(arr) < 3:
#         return None
#     most_freq = np.bincount(arr).argmax()
#     if  most_freq.item() != Inventory.inventory[store_id][barCode][1]:
#         Warnings.warnings[store_id] = Warnings.warnings[store_id] +1
#     if Warnings.warnings[store_id] > 7:
#         Sanctions.sanctinos.add(store_id)
#
#
#
# def ItemPrice(item, store_id):
#     if store_id in Inventory.inventory:
#         if item['barCode'] in Inventory.inventory[store_id]:
#             store_price = Inventory.inventory[store_id][item['barCode']][1]
#             predictedPrice = predict(store_id, item['barCode'])
#             if predictedPrice is not None and predictedPrice != store_price:
#                 return predictedPrice
#             else:
#                 return store_price
#
#
# def cartPrice(cart, store_id):
#     sumPrice = 0
#     prices = []
#     for item in cart:
#         if store_id in Inventory.inventory.keys():
#             itemPrice = ItemPrice(item, store_id) * int(item['count'])
#             sumPrice += itemPrice
#             prices.append(itemPrice)
#         else: sumPrice = 1000000
#
#     return sumPrice, prices
#
# @api_view(['GET'])
# def suggest(request, current_store_id):
#     Carts = Cart.objects.all()
#     serializer = CartSerializer(Carts, many=True)
#     #return Response(serializer.data)\
#     data = []
#     for store_id in Stores.stores.keys():
#         price, prices = cartPrice(serializer.data, store_id)
#         if store_id == current_store_id:
#             current_store_result = (store_id, price, prices)
#         data.append((store_id, price, prices))
#
#     best = min(data, key=operator.itemgetter(1))
#     best_store, best_price, best_prices = best
#     best_store_name = Stores.stores[best_store][0]
#     if best_store == current_store_id:
#         return Response('you are buying from the best store: '+ best_store_name)
#     else:
#         savings = current_store_result[1] - best_price
#         return Response('we reccomend gotin to store: ' + best_store_name + '. to save: ' + str(savings))
#
#
#
# def predict(store_id, barCode):
#     arr = sorted(Reports.reports[store_id][barCode])
#     if arr is None or len(arr) == 0:
#         return None
#     b = len(arr)
#     arr = np.array(arr)
#     a = int(0.05*b)
#     arr = arr[a:b-a]
#     if len(arr) < 3:
#         return None
#     most_freq = np.bincount(arr).argmax()
#     return most_freq.item()
