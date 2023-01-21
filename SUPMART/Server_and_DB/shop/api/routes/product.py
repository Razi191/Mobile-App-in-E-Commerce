import logging

from django.core.exceptions import ValidationError
from django.forms import model_to_dict
from django.http import JsonResponse, HttpResponse
from rest_framework.decorators import api_view
from rest_framework.request import Request

from api.db.models import Product, Store, Inventory
from api.db.serializers import ProductSerializer


@api_view(['POST'])
def create_product(request: Request):
    try:
        product_serializer: ProductSerializer = ProductSerializer(data=request.data)
        if product_serializer.is_valid():
            product_serializer.save()
            return HttpResponse('Created', status=201)
        else:
            errors_arr = [(lambda error: error.__class)(error) for error in product_serializer.errors]
            logging.log(level=logging.ERROR, msg=f'{product_serializer.errors}. {errors_arr}')
            raise ValidationError

    except Exception:
        return HttpResponse('Bad request', status=400)


@api_view(['GET'])
def get_product_id_by_barcode(request: Request):
    product: Product = Product.objects.get(barcode=request.query_params['barcode'])
    return JsonResponse(model_to_dict(product))

    
@api_view(['GET'])
def get_product_barcode_by_id(request: Request):
    product: Product = Product.objects.get(id=request.query_params['id'])
    return JsonResponse(model_to_dict(product))

@api_view(['GET'])
def get_store_price(request: Request, store_id: int):
    store: Store = Store.objects.get(id=store_id)
    product: Product = Product.objects.get(barcode=request.query_params['barcode'])
    inventory_item = Inventory.objects.get(store=store.id, product=product.id)
    return HttpResponse(f'{product.name} price is {inventory_item.price} in store {store.name} in {store.location}.',
                        status=200)


@api_view(['GET'])
def get_products(request: Request):
    return JsonResponse(list(map(lambda product: model_to_dict(product), Product.objects.all())), safe=False)


