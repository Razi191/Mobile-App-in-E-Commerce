import sys

from django.core.exceptions import ValidationError
from django.forms import model_to_dict
from django.http import JsonResponse, HttpResponse
from rest_framework.decorators import api_view
from rest_framework.request import Request

from api.db.models import Store
from api.db.serializers import StoreSerializer
import logging

MAX_PUNISHMENT_POINTS: int = 10


class StoreAlreadyExistsError(Exception):
    pass


logging.basicConfig(stream=sys.stdout, encoding='utf-8', level=logging.DEBUG)


@api_view(['POST'])
def create_store(request: Request):
    try:
        store_serializer: StoreSerializer = StoreSerializer(data=request.data)
        logging.log(level=logging.DEBUG, msg='Successfully serialized request body')
        if store_serializer.is_valid():
            logging.log(level=logging.DEBUG, msg='Request data is valid!')
            store_serializer.save()
            logging.log(level=logging.DEBUG, msg='Request data successfully stored in DB.')
            return HttpResponse('Created', status=201)
        else:
            logging.log(level=logging.ERROR, msg=f'{store_serializer.errors}')
            raise ValidationError

    except Exception as e:
        logging.log(logging.DEBUG, msg=f'If you r here, then u made a crappy request of type {type(e).__name__}.')
        return HttpResponse('Bad request', status=400)


@api_view(['GET'])
def get_store(request: Request):
    store: Store = Store.objects.get(name=request.query_params['name'], location=request.query_params['location'])
    return JsonResponse(model_to_dict(store))


@api_view(['GET'])
def get_store_by_id(request: Request, store_id: int):
    store: Store = Store.objects.get(id=store_id)
    return JsonResponse(model_to_dict(store))


@api_view(['GET'])
def get_store_punishments(request: Request, store_id: int):
    store: Store = Store.objects.get(id=store_id)
    return HttpResponse(f'Store {store.name} in {store.location} has {store.punishment} punishment points.')


def is_punished(store: Store):
    return store.punishment >= MAX_PUNISHMENT_POINTS


@api_view(['GET'])
def is_punished_route(request: Request, store_id: int):
    store: Store = Store.objects.get(id=store_id)
    return JsonResponse({'Punished': f'{is_punished(store)}'})


@api_view(['GET'])
def get_stores(request: Request):
    return JsonResponse(list(map(lambda store: model_to_dict(store), Store.objects.all())), safe=False)
