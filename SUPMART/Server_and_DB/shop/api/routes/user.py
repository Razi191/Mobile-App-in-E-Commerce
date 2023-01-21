import logging

from django.forms import model_to_dict
from django.http import HttpResponse, JsonResponse
from rest_framework.decorators import api_view
from rest_framework.exceptions import ValidationError
from rest_framework.request import Request

from api.db.models import User
from api.db.serializers import UserSerializer


# TODO: remove to a utils dir
class AlreadyExistsError(Exception):
    pass


@api_view(['POST'])
def create_user(request: Request):
    try:
        user_serializer: UserSerializer = UserSerializer(data=request.data)
        if user_serializer.is_valid():
            user_serializer.save()
            return HttpResponse('Created', status=201)
        else:
            raise ValidationError

    except Exception:
        return HttpResponse('Bad request', status=400)


@api_view(['GET'])
def get_user(request: Request, username: str):
    try:
        user: User = User.objects.get(username=username)
        return JsonResponse(model_to_dict(user))
    except Exception as e:
        logging.log(level=logging.ERROR, msg=type(e))
        return HttpResponse('Bad request', status=400)


def _update_username(username: str, user: User) -> None:
    try:
        user.username = username
        user.save()
    except Exception as e:
        logging.log(level=logging.ERROR, msg=type(e))
        raise AlreadyExistsError()


def _update_password(password: str, user: User) -> None:
    try:
        user.password = password
        user.save()
    except Exception as e:
        logging.log(level=logging.ERROR, msg=type(e))
        raise AlreadyExistsError()


@api_view(['PUT'])
def update_user(request: Request, username: str):
    try:
        user: User = User.objects.get(username=username)
        user.password = request.data['password']
        user.save()
        return HttpResponse('Updated Successfully!', status=200)
    except Exception as e:
        logging.log(level=logging.ERROR, msg=type(e))
        return HttpResponse('Conflict', status=409)


@api_view(['DELETE'])
def delete_user(request: Request, username: str):
    try:
        user: User = User.objects.get(username=username)
        user.delete()
        return HttpResponse('Deleted Successfully!', status=200)
    except Exception as e:
        logging.log(level=logging.ERROR, msg=type(e))
        return HttpResponse('Already deleted', status=410)
