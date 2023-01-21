# import json
#
# from django.core.exceptions import ValidationError
# from rest_framework.decorators import api_view
# from rest_framework.response import Response
# from django.http import *
# from django.shortcuts import render
# from .serializers import CartSerializer
# from .models import Cart
# from .models import Inventory
# from .models import Store
# from .models import Report
# from .models import Sanction
# from .views import predict
# from .serializers import *
#
#
# # @api_view(['POST', 'GET'])
# # def set_price(request, store_id, barCode, price):
# #     if store_id in Sanctions.sanctinos:
# #         return Response('User is sanctioned.')
# #     if store_id in Inventory.inventory:
# #         if barCode in Inventory.inventory[store_id]:
# #             Inventory.inventory[store_id][barCode] = (Inventory.inventory[store_id][barCode][0], price)
# #             Reports.reports[store_id][barCode] = []  ## since he updated the item, this item reports will be empty
# #             return Response('price changed.')
# #         else:
# #             return Response('Item does not exist yet.')
# #     else:
# #         return Response('store does not exist.')
#
#
# @api_view(['POST', 'GET'])
# def set_price(request, store_id, barCode, price):
#     pass
#
#
#
# # @api_view(['POST'])
# # def add_store(request):
# #     try:
# #         store_serializer: StoreSerializers = StoreSerializers(data=request.data)
# #         if store_serializer.is_valid():
# #             store_serializer.save()
# #             return HttpResponse('Created', status=201)
# #         raise StoreAlreadyExistsError
# #
# #     except StoreAlreadyExistsError:
# #         return HttpResponse('Already Exists', status=409)
# #
# #     except ValidationError:
# #         return HttpResponse('Bad request', status=400)
#
#     # if Stores.storeNameExist(store_name):
#     #     id = Stores.getStoreID()
#     #     Inventory.inventory[id] = {}
#     #     Reports.reports[id] = {}
#     #     Warnings.warnings[id] = 0
#     #     Stores.stores[id] = (store_name, location_id, password)
#     #     return Response('store added:' + id)
#     # else:
#     #     return Response('store already exists.')
#
#
# @api_view(['GET'])
# def signIn(request, store_id, password):
#     if Stores.stores[store_id][2] == password:
#         return Response('Granted')
#     else:
#         return Response('Rejected')
#
#
# @api_view(['GET'])
# def viewWarnings(request, store_id):
#     if store_id in Warnings.warnings:
#         number = Warnings.warnings[store_id]
#         return Response('number of warnings is: ' + str(number))
#     else:
#         return Response('there are no warnings')
#
#
# @api_view(['GET'])
# def deleteItem(request, store_id, barCode):
#     if store_id in Sanctions.sanctinos:
#         return Response('User is sanctioned.')
#
#     if store_id in Inventory.inventory:
#         if barCode in Inventory.inventory[store_id]:
#             del Inventory.inventory[store_id][barCode]
#             del Reports.reports[store_id][barCode]
#         else:
#             return Response('Item does not exist')
#     else:
#         return Response('store does not exist')
#
#
# @api_view(['GET'])
# def addItem(request, store_id, barCode, price, name):
#     if store_id in Sanctions.sanctinos:
#         return Response('User is sanctioned.')
#     if store_id in Inventory.inventory:
#         if barCode in Inventory.inventory[store_id]:
#             return Response('Item already exists.')
#         else:
#             Inventory.inventory[store_id][barCode] = (name, price)
#             Reports.reports[store_id][barCode] = []  ## since he updated the item, this item reports will be empty
#             return Response('Item added.')
#     else:
#         return Response('store does not exist.')
#
#
# @api_view(['GET'])
# def viewItems(request, store_id):
#     arr = []
#     for barcode in Inventory.inventory[store_id]:
#         name = Inventory.inventory[store_id][barcode][0]
#         price = Inventory.inventory[store_id][barcode][1]
#         arr.append((name, barcode, price))
#     return Response(arr)
#
#
# @api_view(['GET'])
# def viewReports(request, store_id):
#     arr = []
#     for barcode in Reports.reports[store_id]:
#         name = Inventory.inventory[store_id][barcode][0]
#         price = Inventory.inventory[store_id][barcode][1]
#         predicted = predict(store_id, barcode)
#         arr.append((name, barcode, price, predicted))
#         return Response(arr)
