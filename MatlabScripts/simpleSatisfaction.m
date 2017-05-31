function simpleSatisfaction()
% Generates a figure with four satisfactions GA, MO, CLO, RAN

cd('../General');
% Initialization
clear ; close all; clc
X = load('satisfaction.txt');
% Max element of each GA Satisfaction row
XX = max(X(X(:,1)==1,3:end)');
[A,ind] = max(XX); % Index of the max element to plot this run
SGA = X(ind,3:end); % row to print

A = grpstats(X, [X(:,1)]);
steps = [1:1:600];
% Create a line plot with the satisfaction
% plot(steps, A(1,3:end), steps, SGA,steps, A(3,3:end))
plot(steps, A(1,3:end), steps, SGA,steps, A(3,3:end), steps, A(4,3:end))
hold on;
xlabel('time steps')
ylabel('satisfaction')
%legend('random')
title('Satisfaction Comparative','FontSize',12)
legend('random','GA','close', 'multi','Location','NorthWest')
handle=gcf;
saveas(handle, ['sat', 'eps'],'epsc')
hold off;
cd('../Functions');
end