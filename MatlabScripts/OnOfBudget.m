function OnOfBudget()
%% Plot the function with the average remaining Budget
cd('../General');
X = load('budgetONOF.txt');
A = grpstats(X, [X(:,1)]); 
steps = [1:1:600];
A1=A(1,3:end);
A2=A(2,3:end);

figure % create new figure
hold on;
hh=plot(steps(1:10:end), A1(1:10:end), '*', steps(1:10:end), A2(1:10:end), 'o');
set (hh, 'LineWidth', 2)
h=plot(steps, A(1,2:end), '--', steps, A(2,2:end), '--');
set (h, 'LineWidth', 1)


ylabel('Remaining Budget','fontweight','bold','fontsize',14)
xlabel('time steps','fontweight','bold','fontsize',14)
title('Budget not spent (Scenario1)','fontweight','bold','fontsize',16)
legend('ONLINE','OFFLINE','Location','NorthWest')

handle=gcf;
%saveas(handle, ['remainingBudget', 'png'],'png')
saveas(handle, ['remainingBudget2', 'eps'],'epsc2')
hold off;
cd('../Functions');

end

