## Клиент консольного чата

Клиент использует многопоточную модель обработки сообщений. В процессе работы запускает следующие потоки:

1. получение сообщений из консоли `ru.netology.consolechat.client.ConsolechatClient` и инициация других потоков
2. чтение входящих сообщение от сервера `ru.netology.consolechat.common.worker.ReceiveWorker`. Получив сообщение от
   сервера, воркер отправялет его в очереди всех потребителей: воркер вывода сообщения в консоль, воркер записи
   сообщений в логфайл
3. отправка сообщений на сервер чата `ru.netology.consolechat.common.worker.SendWorker`
4. запись в лог файл полученных сообщений `ru.netology.consolechat.common.worker.LogWorker`
5. вывод сообщений в консоль `ru.netology.consolechat.client.ConsoleOutputWorker`

При запуске клиент использует конфигурацию из файла хранящегося в `etc/client.conf`. Директория etc должна находиться
в папке с запускаемым jar файлом или в папке проекта. Пример конфигурации см в `etc/client.conf.example`.

```ini
host=localhost
port=8080
messageQueueCapacity=10
logfile=log/messages-client-log.txt
```

messageQueueCapacity - максимальная длина очередей сообщений между воркерами.
