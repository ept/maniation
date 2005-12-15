function newstatus = rigidcube(status, time)
    newstatus = rigidbody(status, time, 1, eye(3)/6.0, []);
endfunction
