import { TestBed, async } from '@angular/core/testing';
import { UserService } from './gen/user.service';
import { HttpClientModule } from '@angular/common/http';
import { CRUD_BASE_URL } from './app.module';

let userService: UserService;

describe('UserService', () => {

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                HttpClientModule
            ],
            providers: [
                { provide: CRUD_BASE_URL, useValue: '/api' },
                UserService
            ],
        }).compileComponents();
        userService = TestBed.get(UserService);
    }));



    it('should get empty list of users', async(() => {

        userService.findAllUsers('x', null).then((result) => {
            expect(result).toEqual([]);
        });

    }));

    it('should create user', async(() => {

        const user = { id: 1, firstname: 'Donald', lastname: 'Duck' };
        userService.createUser(user).then((result) => {
            expect(result).toEqual(user);
        });

    }));

    it('should get list with created user', async(() => {

        userService.findAllUsers('x', null).then((result) => {
            expect(result).toEqual([{id: 1, firstname: 'Donald', lastname: 'Duck'}]);
        });

    }));

    it('should delete existing user', async(() => {

        const user = { id: 1, firstname: 'Donald', lastname: 'Duck' };
        userService.deleteUserById(1).then((result) => {
            expect(result).toBeNull();
        });
    }));

    it('should get empty list of users after deletion', async(() => {

        userService.findAllUsers('x', null).then((result) => {
            expect(result).toEqual([]);
        });

    }));
});
