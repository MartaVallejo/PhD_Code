function remainingBudget()
%% Plot the function with the average remaining Budget
cd('../General');
X = load('remainBudget.txt');
A = grpstats(X, [X(:,1)]); 
steps = [1:1:599];
A1=A(1,3:end);
A2=A(2,3:end);
A3=A(3,3:end);

figure % create new figure
hold on;
%h=plot(steps, A1(1,2:end), '-', steps, A2(2,2:end), '-',steps, A3(3,2:end), '-');
%set (h, 'LineWidth', 1)
hh=plot(steps(1:4:end), A1(1:4:end), '--', steps(1:4:end), A2(1:4:end), '--', steps(1:4:end), A3(3:4:end), '--');
set (hh, 'LineWidth', 2)

ylabel('Remaining Budget','fontweight','bold','fontsize',14)
xlabel('Tick of the simulation','fontweight','bold','fontsize',14)
title('Budget not spent (Scenario1)','fontweight','bold','fontsize',16)
legend('RAN','GA', 'CLO','Location','NorthWest')

handle=gcf;
%saveas(handle, ['remainingBudget', 'png'],'png')
saveas(handle, ['remainingBudget', 'eps'],'epsc2')
hold off;
cd('../Functions');

end

