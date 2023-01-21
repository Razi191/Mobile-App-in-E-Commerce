import json
import logging
from typing import Dict
import sys
from django.core.serializers import serialize
from django.db.models import QuerySet
from django.http import HttpResponse, JsonResponse
from rest_framework.decorators import api_view
from rest_framework.exceptions import ValidationError
from rest_framework.request import Request

from api.db.models import Cart, User, Product, Store, Inventory
from api.db.serializers import CartItemSerializer, CartItemListSerializer
from api.routes.store import is_punished


@api_view(['POST'])
def add_cart_item(request: Request):
    try:
        cart_serializer: CartItemSerializer = CartItemSerializer(data=request.data, partial=True)
        if cart_serializer.is_valid():
            logging.log(level=logging.DEBUG, msg='validation is a success')
            cart_serializer.save()
            return HttpResponse('Created', status=201)
        else:
            logging.log(level=logging.ERROR, msg=f'{cart_serializer.errors}.')
            raise ValidationError
    except Exception as e:
        logging.log(level=logging.ERROR, msg=f'Exception type is {type(e)}. {e.__class__}')
        return HttpResponse('Bad request', status=400)


@api_view(['GET'])
def get_cart(request: Request, user_id: int):
    cart: QuerySet = Cart.objects.filter(user=User.objects.get(id=user_id).id).all()
    return JsonResponse(json.loads(serialize(format='json', queryset=cart)), safe=False)


@api_view(['PATCH'])
def update_cart_quantity(request: Request, user_id: int):
    """
    This method receives a list of products and creates/updates them
    """
    try:
        cart: Cart = Cart.objects.get(user=user_id)
        if cart:
            cart_serializer: CartItemSerializer = CartItemSerializer(instance=cart,
                                                                     data=request.data,
                                                                     partial=True)
            if cart_serializer.is_valid(raise_exception=False):
                cart_serializer.save()

            return HttpResponse('Successfully updated!', status=200)
    except Exception:
        return HttpResponse('Bad request', status=400)


@api_view(['DELETE'])
def delete_cart_item(request: Request, user_id: int):
    try:
        cart_item: Cart = Cart.objects.get(user=user_id, product=request.data['product'])
        cart_item.delete()
        return HttpResponse('Deleted Successfully!', status=200)
    except Exception:
        return HttpResponse('Bad request', status=400)


@api_view(['DELETE'])
def delete_cart(request: Request, user_id: int):
    try:
        cart: QuerySet = Cart.objects.filter(user=user_id).all()
        cart.delete()
        return HttpResponse('Deleted Successfully!', status=200)
    except Exception:
        return HttpResponse('Bad request', status=400)


@api_view(['GET'])
def suggest(request: Request, user_id: int):
    try:
        cart_items: QuerySet = Cart.objects.filter(user=user_id).all()
        all_stores: QuerySet = Store.objects.all()
        store_to_bill: Dict[Store, float] = {}
        for store in all_stores:
            logging.log(level=logging.DEBUG, msg=f'store is {store.id}')
            store_cart_bill: float = 0
            for item in cart_items:
                logging.log(level=logging.DEBUG, msg=f'item is {item.product}')
                try:
                    inventory_item: Inventory = Inventory.objects.get(store=store.id, product=item.product)
                    store_cart_bill += item.quantity * inventory_item.price
                except Exception as e:
                    logging.log(level=logging.ERROR, msg=f'Error is a {e.__class__}')
                    store_cart_bill = sys.maxsize
                    break
            store_to_bill[store] = store_cart_bill
        cheapest_store: Store = min(store_to_bill, key=store_to_bill.get)
        if store_to_bill[cheapest_store] == sys.maxsize:
            return HttpResponse('Unfortunately, there is no store that can supply you with your products.')
        return HttpResponse(
            f'You should buy in {cheapest_store.name}, {cheapest_store.location}. The price is {store_to_bill[cheapest_store]}',
            status=200)
    except Exception as e:
        logging.log(level=logging.ERROR, msg=f'This shit\'t type is {type(e)}')
        return HttpResponse('Server Error', status=500)
