import json
import logging
from django.db.models import QuerySet
from django.http import HttpResponse, JsonResponse
from rest_framework.decorators import api_view
from rest_framework.exceptions import ValidationError
from rest_framework.request import Request
from django.core.serializers import serialize
from api.db.models import Inventory, Store, Product
from api.db.serializers import InventorySerializer
from api.routes.store import is_punished


@api_view(['POST'])
def create_store_inventory_record(request: Request):
    """
    This method creates a record of store-product with price and quantity of the product.
    A store has an inventory iff it has at least one product in it (i.e. one record)
    """
    try:
        inventory_serializer: InventorySerializer = InventorySerializer(data=request.data, partial=True)
        if inventory_serializer.is_valid():
            logging.log(level=logging.DEBUG, msg='validation is a success')
            store: Store = inventory_serializer.validated_data['store']
            logging.log(level=logging.DEBUG, msg='we got the store!')
            if not is_punished(store):
                inventory_serializer.save()
                return HttpResponse('Created', status=201)
            return HttpResponse('Request declined, Store is punished!', status=403)
        else:
            logging.log(level=logging.ERROR, msg=f'{inventory_serializer.errors}.')
            raise ValidationError

    except Exception as e:
        logging.log(level=logging.ERROR, msg=f'Exception type is {type(e)}. {e.__class__}')
        return HttpResponse('Bad request', status=400)


@api_view(['GET'])
def get_inventory(request: Request, store_id: int):
    inventory: QuerySet = Inventory.objects.filter(store=Store.objects.filter(id=store_id).first()).all()
    return JsonResponse(json.loads(serialize(format='json', queryset=inventory)), safe=False)


@api_view(['POST'])
def update_inventory_price_quantity(request: Request, store_id: int, product_id: int):
    """
    This method updates a price and quantity of a given product in a store
    """
    try:
        inventory_item: Inventory = Inventory.objects.get(store=store_id, product=product_id)
        if inventory_item:
            inventory_serializer: InventorySerializer = InventorySerializer(instance=inventory_item
                                                                            , data=request.data,
                                                                            partial=True)
            if inventory_serializer.is_valid(raise_exception=False):
                if not is_punished(inventory_item.store):
                    inventory_serializer.save()
                    return HttpResponse('Updated successfully', status=200)
                return HttpResponse('Request declined, Store is punished!', status=403)

        else:
            return HttpResponse('Store or Product Not found', status=404)

    except Exception as e:
        logging.log(level=logging.ERROR, msg=f'{e.__class__}.')
        return HttpResponse('Bad request', status=400)


@api_view(['GET'])
def delete_inventory_record(request: Request, store_id: int, product_id: int):
    try:
        inventory: Inventory = Inventory.objects.get(store=store_id, product=product_id)
        inventory.delete()
        return HttpResponse('Deleted Successfully!', status=200)
    except Exception:
        return HttpResponse('Bad request', status=400)

