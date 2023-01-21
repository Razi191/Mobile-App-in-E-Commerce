import logging


from django.forms import model_to_dict
from django.http import HttpResponse, JsonResponse
from django.db import transaction
from rest_framework.decorators import api_view
from rest_framework.exceptions import ValidationError
from rest_framework.request import Request

from api.db.models import Store, Inventory, Report
from api.db.serializers import ReportSerializer


@api_view(['POST'])
def create_report(request: Request):
    try:
        report_serializer: ReportSerializer = ReportSerializer(data=request.data)
        if report_serializer.is_valid():
            real_price: float = report_serializer.validated_data['real_price']
            inventory: Inventory = Inventory.objects.get(store=report_serializer.validated_data['store'],
                                                         product=report_serializer.validated_data['product'])

            if real_price > inventory.price:
                report_serializer.save()
                store: Store = report_serializer.validated_data['store']
                store.punishment += 1
                # inventory.price = real_price
                with transaction.atomic():
                    store.save()
                    inventory.save()
                return HttpResponse('Created', status=201)
        else:
            logging.log(level=logging.ERROR, msg=f'{report_serializer.errors}.')
            raise ValidationError

    except Exception as e:
        logging.log(level=logging.ERROR, msg=f'Exception type is {type(e)}. {e.__class__}')
        return HttpResponse('Bad request', status=400)


@api_view(['GET'])
def get_reports_by_store(request: Request):
    return JsonResponse(list(map(lambda report: model_to_dict(report), Report.objects.filter(store=request.query_params['store_id']).all())),
                        safe=False)


@api_view(['GET'])
def predict(request: Request, store_id: int, product_id: int):
    store_reports: Report = Report.objects.get(store=store_id).all()

