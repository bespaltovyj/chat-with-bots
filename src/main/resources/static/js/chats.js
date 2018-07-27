var ws;
$(function(){
    window.addEventListener('unload', function(event) {
      if (ws) {
      ws.close();
      }
    });
    $("#createChatRoomBtn").click(function() {
        if (!ws) {
          return;
        }
        var createRoomMessage = {};
        createRoomMessage.type = "CREATE_ROOM";
        ws.send(JSON.stringify(createRoomMessage));
    });
    $("#addUserBtn").click(function() {
      if (!ws) {
        return;
      }
      var user = $("#users-list").find("a.active").html();
      if (!user) {
        alert("Пользователь не выбран")
        return;
      }
      var chat = $("#chats-tab").find("a.active").html();
      if (!chat) {
        alert("Чат не выбран")
        return;
      }
      var addUserMsg = {};
      addUserMsg.type = "ADD_USER";
      addUserMsg.userLogin = user;
      addUserMsg.roomId = chat;
      ws.send(JSON.stringify(addUserMsg));
    });
    connect();
});


function connect(){
    ws = new SockJS('/chats-socket');
    ws.onopen = function () {
	    console.log('Info: WebSocket connection opened.');
	    $("#createChatRoomBtn").prop("disabled", false);
	    $("#addUserBtn").prop("disabled", false);
    };
    ws.onmessage = function (event) {
	    var response = JSON.parse(event.data);
	    if (response.type == "USERS_ONLINE") {
	        var usersOnline = JSON.parse(response.content);
	        usersOnline.forEach(function(item, i, arr) {
              $("#users-list").append(createUserTag(item));
            });
	    }

	    if (response.type == "USER_LOGIN") {
            $("#users-list").append(createUserTag(response.userLogin));
        }
        if (response.type == "USER_LOGOUT") {
            $("#users-list").find("a").filter( function(i, el) {
                return $(el).html() == response.userLogin;
            }
            ).remove();
        }
        if (response.type == "SEND_MESSAGE") {
            addMessageToRoom(response);
        }
        if (response.type == "CREATE_ROOM") {
            createChatRoom(response.roomId);
        }
        if (response.type == "ADD_USER") {
            addUserJoinedMessageToRoom(response);
        }
    };
    ws.onclose = function () {
	    console.log('Info: WebSocket connection closed.');
    };
}

function createUserTag(login) {
    return '<a class="list-group-item list-group-item-action" data-toggle="list" role="tab">' + login + '</a>';
}

function createChatRoom(roomId) {
    $("#chats-tabContent").append(
        '<div class="tab-pane" role="tabpanel" id="chat-tab' + roomId + '">'
            + '<textarea class="form-control" readonly="readonly" style="height: 400px; resize: none"></textarea>'
            + '<div class="input-group">'
                + '<input type="text" class="form-control placeholder="Input Message"></input>'
                + '<div class="input-group-append">'
                    + '<button class="btn btn-outline-secondary" type="button" onclick="sendMessage(this)" data-roomid="' + roomId + '">Отправить</button>'
                + '</div>'
            + '</div>'
        + '</div>'
    );
    $("#chats-tab").append(
        '<a class="nav-link" data-toggle="pill" role="tab" href="#chat-tab'+roomId+'">' + roomId + '</a>'
    );
}



function sendMessage(el) {
    if (!ws) {
        return;
    }
    var roomId = $(el).data("roomid");
    var message = {};
    message.type = "SEND_MESSAGE";
    message.roomId = roomId;
    message.content = $("#chat-tab" + roomId).find("input").val();
    ws.send(JSON.stringify(message));

    $("#chat-tab" + roomId).find("input").val("");
}

function addMessageToRoom(systemMessage) {
    $("#chat-tab" + systemMessage.roomId).find("textarea").append(systemMessage.userLogin + ": " + systemMessage.content + "\n");
}

function addUserJoinedMessageToRoom(systemMessage) {
    $("#chat-tab" + systemMessage.roomId).find("textarea").append("Пользователь " + systemMessage.userLogin + " присоеденился к чату "  + "\n");
}

